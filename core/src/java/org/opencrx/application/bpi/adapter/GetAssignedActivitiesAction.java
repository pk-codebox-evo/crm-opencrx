/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Description: GetAssignedActivitiesAction
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * ====================================================================
 *
 * This software is published under the BSD license
 * as listed below.
 * 
 * Copyright (c) 2013, CRIXP Corp., Switzerland
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions 
 * are met:
 * 
 * * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 
 * * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in
 * the documentation and/or other materials provided with the
 * distribution.
 * 
 * * Neither the name of CRIXP Corp. nor the names of the contributors
 * to openCRX may be used to endorse or promote products derived
 * from this software without specific prior written permission
 * 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND
 * CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * ------------------
 * 
 * This product includes software developed by the Apache Software
 * Foundation (http://www.apache.org/).
 * 
 * This product includes software developed by contributors to
 * openMDX (http://www.openmdx.org/)
 */
package org.opencrx.application.bpi.adapter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.opencrx.application.bpi.datatype.BpiActivity;
import org.opencrx.kernel.account1.jmi1.Contact;
import org.opencrx.kernel.activity1.cci2.ActivityQuery;
import org.opencrx.kernel.activity1.jmi1.Activity;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.naming.Path;

/**
 * Get Contact's assigned activities action.
 *
 */
public class GetAssignedActivitiesAction extends BpiAction {

	/* (non-Javadoc)
	 * @see org.opencrx.application.bpi.adapter.BpiAdapterServlet.Action#handle(org.openmdx.base.naming.Path, javax.jdo.PersistenceManager, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
    public void perform(
    	Path path, PersistenceManager pm,
    	BpiPlugIn plugIn,    	
    	HttpServletRequest req, 
    	HttpServletResponse resp
    ) throws IOException, ServiceException {
    	List<Contact> contacts = plugIn.findContacts(path.getPrefix(7), pm);
    	if(contacts == null || contacts.isEmpty()) {
    		resp.setStatus(HttpServletResponse.SC_NOT_FOUND); 
    	} else {
    		Contact contact = contacts.iterator().next();
    		List<BpiActivity> bpiAssignedActivities = new ArrayList<BpiActivity>();
    		ActivityQuery activityQuery = (ActivityQuery)pm.newQuery(Activity.class);
    		activityQuery.orderByCreatedAt().descending();
    		activityQuery.forAllDisabled().isFalse();
    		int count = 0;
    		for(Activity activity: contact.<Activity>getAssignedActivity(activityQuery)) {
    			bpiAssignedActivities.add(
    				plugIn.toBpiActivity(activity, plugIn.newBpiActivity(), false)
    			);
    			count++;
    			if(count > 500) {
    				break;
    			}
    		}
    		resp.setCharacterEncoding("UTF-8");
    		resp.setContentType("application/json");
    		PrintWriter pw = resp.getWriter();
    		plugIn.printObject(pw, bpiAssignedActivities);
    		resp.setStatus(HttpServletResponse.SC_OK);
    	}
    }

}