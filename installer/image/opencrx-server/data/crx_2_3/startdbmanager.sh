#!/bin/sh
cd @@INSTALLDIR@@/data/crx_2_3
java -cp ../hsqldb.jar org.hsqldb.util.DatabaseManagerSwing --url jdbc:hsqldb:hsql://127.0.0.1:@@HSQLDB_PORT@@/CRX_2_3 --user sa --password manager99