/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License. 
 *  
 */
package org.apache.directory.server.core.interceptor.context;


import java.util.Set;

import javax.naming.directory.SearchControls;

import org.apache.directory.server.core.CoreSession;
import org.apache.directory.shared.ldap.filter.ExprNode;
import org.apache.directory.shared.ldap.filter.SearchScope;
import org.apache.directory.shared.ldap.message.AliasDerefMode;
import org.apache.directory.shared.ldap.message.MessageTypeEnum;
import org.apache.directory.shared.ldap.name.LdapDN;
import org.apache.directory.shared.ldap.schema.AttributeTypeOptions;


/**
 * A Search context used for Interceptors. It contains all the informations
 * needed for the search operation, and used by all the interceptors
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class SearchOperationContext extends SearchingOperationContext
{
    /** The filter */
    private ExprNode filter;


    /**
     * Creates a new instance of SearchOperationContext.
     */
    public SearchOperationContext( CoreSession session )
    {
    	super( session );
    }


    /**
     * Creates a new instance of SearchOperationContext.
     * 
     * @param aliasDerefMode the alias dereferencing mode
     * @param dn the dn of the search base
     * @param filter the filter AST to use for the search
     * @param searchControls the search controls
     */
    public SearchOperationContext( CoreSession session, LdapDN dn, AliasDerefMode aliasDerefMode, ExprNode filter,
                                   SearchControls searchControls ) throws Exception
    {
        super( session, dn, aliasDerefMode, searchControls );
        this.filter = filter;
    }


    /**
     * Creates a new instance of SearchOperationContext.
     * 
     * @param session the session this operation is associated with
     * @param dn the search base
     * @param scope the search scope
     * @param filter the filter AST to use for the search
     * @param aliasDerefMode the alias dereferencing mode
     * @param returningAttributes the attributes to return
     */
    public SearchOperationContext( CoreSession session, LdapDN dn, SearchScope scope,
        ExprNode filter, AliasDerefMode aliasDerefMode, Set<AttributeTypeOptions> returningAttributes )
    {
        super( session, dn, aliasDerefMode, returningAttributes );
        super.setScope( scope );
        this.filter = filter;
    }


    /**
     * @return The filter
     */
    public ExprNode getFilter()
    {
        return filter;
    }


    /**
     * Set the filter into the context.
     *
     * @param filter The filter to set
     */
    public void setFilter( ExprNode filter )
    {
        this.filter = filter;
    }


    /**
     * @see Object#toString()
     */
    public String toString()
    {
        return "SearchContext for DN '" + getDn().getUpName() + "', filter :'"
        + filter + "'"; 
    }


    /**
     * @return the operation name
     */
    public String getName()
    {
        return MessageTypeEnum.SEARCH_REQUEST.name();
    }
}
