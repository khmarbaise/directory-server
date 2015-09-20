/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.directory.shared.client.api;


import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.apache.directory.api.ldap.codec.api.SchemaBinaryAttributeDetector;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.ldap.client.api.DefaultPoolableLdapConnectionFactory;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.directory.ldap.client.api.LdapConnectionConfig;
import org.apache.directory.ldap.client.api.LdapConnectionPool;
import org.apache.directory.ldap.client.api.LdapNetworkConnection;
import org.apache.directory.server.constants.ServerDNConstants;
import org.apache.directory.server.ldap.LdapServer;


/**
 * The Class LdapApiIntegrationUtils.
 */
public final class LdapApiIntegrationUtils
{
    /** The Constant DEFAULT_HOST. */
    private static final String DEFAULT_HOST = "localhost";

    /** The Constant DEFAULT_ADMIN. */
    private static final String DEFAULT_ADMIN = ServerDNConstants.ADMIN_SYSTEM_DN;

    /** The Constant DEFAULT_PASSWORD. */
    private static final String DEFAULT_PASSWORD = "secret";

    /** The pools. */
    private static final Map<Integer, LdapConnectionPool> POOLS = new HashMap<Integer, LdapConnectionPool>();


    private LdapApiIntegrationUtils()
    {
    }


    /**
     * Creates a new {@link LdapNetworkConnection} and authenticates as admin user.
     * The caller is responsible for closing the connection, use closeConnection().
     *
     * @param ldapServer the LDAP server instance, used to obtain the port used
     * @return the created connection
     * @throws LdapException the LDAP exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static LdapNetworkConnection createAdminConnection( LdapServer ldapServer ) throws LdapException,
        IOException
    {
        LdapNetworkConnection conn = new LdapNetworkConnection( InetAddress.getLocalHost().getHostName(), ldapServer.getPort() );
        conn.bind( DEFAULT_ADMIN, DEFAULT_PASSWORD );
        return conn;
    }


    /**
     * Closes the {@link LdapNetworkConnection}.
     *
     * @param conn the connection to close
     * @throws LdapException the LDAP exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static void closeConnection( LdapNetworkConnection conn ) throws LdapException, IOException
    {
        if ( conn != null )
        {
            conn.unBind();
            conn.close();
        }
    }


    /**
     * Gets the pooled {@link LdapConnectionPool}, authenticated as admin user.
     * The caller is reponsible for releasing the connection, use releasePooledConnection().
     *
     * @param ldapServer the LDAP server instance, used to obtain the port used
     * @return the pooled admin connection
     * @throws Exception the exception
     */
    public static LdapConnection getPooledAdminConnection( LdapServer ldapServer ) throws Exception
    {
        LdapConnection ldapConnection = getAdminPool( ldapServer ).getConnection();

        ldapConnection.setBinaryAttributeDetector(
            new SchemaBinaryAttributeDetector(
                ldapServer.getDirectoryService().getSchemaManager() ) );

        return ldapConnection;
    }


    /**
     * Releases a pooled connection back to the pool.
     *
     * @param conn the connection to release
     * @param ldapServer the LDAP server instance, used to obtain the port used
     * @throws Exception the exception
     */
    public static void releasePooledAdminConnection( LdapConnection conn, LdapServer ldapServer )
        throws Exception
    {
        getAdminPool( ldapServer ).releaseConnection( conn );
    }


    /**
     * Gets the admin pool.
     *
     * @param ldapServer the ldap server
     * @return the admin pool
     */
    private static LdapConnectionPool getAdminPool( LdapServer ldapServer )
    {
        int port = ldapServer.getPort();

        if ( !POOLS.containsKey( port ) )
        {
            LdapConnectionConfig config = new LdapConnectionConfig();
            
            try
            {
                config.setLdapHost( InetAddress.getLocalHost().getHostName() );
            }
            catch( UnknownHostException uhe )
            {
                config.setLdapHost( DEFAULT_HOST );
            }
            
            config.setLdapPort( port );
            config.setName( DEFAULT_ADMIN );
            config.setCredentials( DEFAULT_PASSWORD );
            DefaultPoolableLdapConnectionFactory factory = new DefaultPoolableLdapConnectionFactory( config );
            LdapConnectionPool pool = new LdapConnectionPool( factory );
            pool.setTestOnBorrow( true );
            POOLS.put( port, pool );
        }

        return POOLS.get( port );
    }


    /**
     * Gets an anonymous LdapNetworkConnection
     * 
     * @param dirService The Directory Service to be connected to
     * @return A LdapNetworkConnection instance
     * @exception If the connection could not be established.
     */
    public static LdapConnection getAnonymousNetworkConnection( String host, int port ) throws Exception
    {
        LdapConnection connection = new LdapNetworkConnection( host, port );
        connection.bind();

        return connection;
    }


    /**
     * Gets an anonymous LdapNetworkConnection
     * 
     * @param ldapServer The LDAP server we want to connect to
     * @return A LdapNetworkConnection instance
     * @exception If the connection could not be established.
     */
    public static LdapConnection getAnonymousNetworkConnection( LdapServer ldapServer ) throws Exception
    {
        LdapConnection connection = new LdapNetworkConnection( InetAddress.getLocalHost().getHostName(), ldapServer.getPort() );
        connection.setTimeOut( 0L );
        connection.bind();

        return connection;
    }
}
