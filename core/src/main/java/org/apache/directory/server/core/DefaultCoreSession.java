/*
 *   Licensed to the Apache Software Foundation (ASF) under one
 *   or more contributor license agreements.  See the NOTICE file
 *   distributed with this work for additional information
 *   regarding copyright ownership.  The ASF licenses this file
 *   to you under the Apache License, Version 2.0 (the
 *   "License"); you may not use this file except in compliance
 *   with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 *
 */
package org.apache.directory.server.core;


import java.net.SocketAddress;
import java.util.List;
import java.util.Set;

import javax.naming.ldap.Control;

import org.apache.directory.server.core.authn.LdapPrincipal;
import org.apache.directory.server.core.entry.ClonedServerEntry;
import org.apache.directory.server.core.entry.ServerEntry;
import org.apache.directory.server.core.filtering.EntryFilteringCursor;
import org.apache.directory.server.core.interceptor.context.AddOperationContext;
import org.apache.directory.server.core.interceptor.context.CompareOperationContext;
import org.apache.directory.server.core.interceptor.context.DeleteOperationContext;
import org.apache.directory.server.core.interceptor.context.ListOperationContext;
import org.apache.directory.server.core.interceptor.context.LookupOperationContext;
import org.apache.directory.server.core.interceptor.context.ModifyOperationContext;
import org.apache.directory.server.core.interceptor.context.MoveAndRenameOperationContext;
import org.apache.directory.server.core.interceptor.context.MoveOperationContext;
import org.apache.directory.server.core.interceptor.context.OperationContext;
import org.apache.directory.server.core.interceptor.context.RenameOperationContext;
import org.apache.directory.server.core.interceptor.context.SearchOperationContext;
import org.apache.directory.shared.ldap.constants.AuthenticationLevel;
import org.apache.directory.shared.ldap.entry.Modification;
import org.apache.directory.shared.ldap.filter.ExprNode;
import org.apache.directory.shared.ldap.filter.SearchScope;
import org.apache.directory.shared.ldap.message.AliasDerefMode;
import org.apache.directory.shared.ldap.name.LdapDN;
import org.apache.directory.shared.ldap.name.Rdn;
import org.apache.directory.shared.ldap.schema.AttributeTypeOptions;


/**
 * The default CoreSession implementation.
 * 
 * TODO - has not been completed yet
 * TODO - need to supply controls and other parameters to setup opContexts
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class DefaultCoreSession implements CoreSession
{
    private final DirectoryService directoryService;
    private final LdapPrincipal authenticatedPrincipal;
    private LdapPrincipal authorizedPrincipal;
    private ReferralHandlingMode referralHandlingMode = ReferralHandlingMode.IGNORE;
    
    
    public DefaultCoreSession( LdapPrincipal principal, DirectoryService directoryService )
    {
        this.directoryService = directoryService;
        this.authenticatedPrincipal = principal;
    }

    
    /* (non-Javadoc)
     * @see org.apache.directory.server.core.CoreSession#add(org.apache.directory.server.core.entry.ServerEntry)
     */
    public void add( ServerEntry entry ) throws Exception
    {
        directoryService.getOperationManager().add( new AddOperationContext( this, entry ) );
    }


    /* (non-Javadoc)
     * @see org.apache.directory.server.core.CoreSession#compare(org.apache.directory.shared.ldap.name.LdapDN, java.lang.String, java.lang.Object)
     */
    public void compare( LdapDN dn, String oid, Object value ) throws Exception
    {
        directoryService.getOperationManager().compare( new CompareOperationContext( this, dn, oid, value ) );
    }


    /* (non-Javadoc)
     * @see org.apache.directory.server.core.CoreSession#delete(org.apache.directory.shared.ldap.name.LdapDN)
     */
    public void delete( LdapDN dn ) throws Exception
    {
        directoryService.getOperationManager().delete( new DeleteOperationContext( this, dn ) );
    }


    /* (non-Javadoc)
     * @see org.apache.directory.server.core.CoreSession#getAuthenticatedPrincipal()
     */
    public LdapPrincipal getAuthenticatedPrincipal()
    {
        return authenticatedPrincipal;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.server.core.CoreSession#getAuthenticationLevel()
     */
    public AuthenticationLevel getAuthenticationLevel()
    {
        return getEffectivePrincipal().getAuthenticationLevel();
    }


    /* (non-Javadoc)
     * @see org.apache.directory.server.core.CoreSession#getClientAddress()
     */
    public SocketAddress getClientAddress()
    {
        // TODO Auto-generated method stub
        return null;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.server.core.CoreSession#getControls()
     */
    public Set<Control> getControls()
    {
        // TODO Auto-generated method stub
        return null;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.server.core.CoreSession#getDirectoryService()
     */
    public DirectoryService getDirectoryService()
    {
        return directoryService;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.server.core.CoreSession#getEffectivePrincipal()
     */
    public LdapPrincipal getEffectivePrincipal()
    {
        if ( authorizedPrincipal == null )
        {
            return authenticatedPrincipal;
        }
        
        return authorizedPrincipal;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.server.core.CoreSession#getOutstandingOperations()
     */
    public Set<OperationContext> getOutstandingOperations()
    {
        // TODO Auto-generated method stub
        return null;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.server.core.CoreSession#getServiceAddress()
     */
    public SocketAddress getServiceAddress()
    {
        // TODO Auto-generated method stub
        return null;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.server.core.CoreSession#isConfidential()
     */
    public boolean isConfidential()
    {
        // TODO Auto-generated method stub
        return false;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.server.core.CoreSession#isVirtual()
     */
    public boolean isVirtual()
    {
        // TODO Auto-generated method stub
        return true;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.server.core.CoreSession#list(org.apache.directory.shared.ldap.name.LdapDN, org.apache.directory.shared.ldap.message.AliasDerefMode, java.util.Set)
     */
    public EntryFilteringCursor list( LdapDN dn, AliasDerefMode aliasDerefMode,
        Set<AttributeTypeOptions> returningAttributes ) throws Exception
    {
        return directoryService.getOperationManager().list( 
            new ListOperationContext( this, dn, aliasDerefMode, returningAttributes ) );
    }


    /* (non-Javadoc)
     * @see org.apache.directory.server.core.CoreSession#list(org.apache.directory.shared.ldap.name.LdapDN, org.apache.directory.shared.ldap.message.AliasDerefMode, java.util.Set, int, int)
     */
    public EntryFilteringCursor list( LdapDN dn, AliasDerefMode aliasDerefMode,
        Set<AttributeTypeOptions> returningAttributes, int sizeLimit, int timeLimit ) throws Exception
    {
        ListOperationContext opContext = new ListOperationContext( this, dn, aliasDerefMode, returningAttributes );
        opContext.setSizeLimit( sizeLimit );
        opContext.setTimeLimit( timeLimit );
        return directoryService.getOperationManager().list( opContext );
    }


    /* (non-Javadoc)
     * @see org.apache.directory.server.core.CoreSession#lookup(org.apache.directory.shared.ldap.name.LdapDN)
     */
    public ClonedServerEntry lookup( LdapDN dn ) throws Exception
    {
        return directoryService.getOperationManager().lookup( new LookupOperationContext( this, dn ) );
    }


    /* (non-Javadoc)
     * @see org.apache.directory.server.core.CoreSession#modify(org.apache.directory.shared.ldap.name.LdapDN, java.util.List)
     */
    public void modify( LdapDN dn, List<Modification> mods ) throws Exception
    {
        directoryService.getOperationManager().modify( new ModifyOperationContext( this, dn, mods ) );
    }


    /* (non-Javadoc)
     * @see org.apache.directory.server.core.CoreSession#move(org.apache.directory.shared.ldap.name.LdapDN, org.apache.directory.shared.ldap.name.LdapDN)
     */
    public void move( LdapDN dn, LdapDN newParent ) throws Exception
    {
        directoryService.getOperationManager().move( new MoveOperationContext( this, dn, newParent ) );
    }


    /* (non-Javadoc)
     * @see org.apache.directory.server.core.CoreSession#moveAndRename(org.apache.directory.shared.ldap.name.LdapDN, org.apache.directory.shared.ldap.name.LdapDN, org.apache.directory.shared.ldap.name.Rdn, boolean)
     */
    public void moveAndRename( LdapDN dn, LdapDN newParent, Rdn newRdn, boolean deleteOldRdn ) throws Exception
    {
        directoryService.getOperationManager().moveAndRename( 
            new MoveAndRenameOperationContext( this, dn, newParent, newRdn, deleteOldRdn ) );
    }


    /* (non-Javadoc)
     * @see org.apache.directory.server.core.CoreSession#rename(org.apache.directory.shared.ldap.name.LdapDN, org.apache.directory.shared.ldap.name.Rdn, boolean)
     */
    public void rename( LdapDN dn, Rdn newRdn, boolean deleteOldRdn ) throws Exception
    {
        directoryService.getOperationManager().rename( new RenameOperationContext( this, dn, newRdn, deleteOldRdn ) );
    }


    /* (non-Javadoc)
     * @see org.apache.directory.server.core.CoreSession#search(org.apache.directory.shared.ldap.name.LdapDN, org.apache.directory.shared.ldap.filter.SearchScope, org.apache.directory.shared.ldap.filter.ExprNode, org.apache.directory.shared.ldap.message.AliasDerefMode, java.util.Set)
     */
    public EntryFilteringCursor search( LdapDN dn, SearchScope scope, ExprNode filter, AliasDerefMode aliasDerefMode,
        Set<AttributeTypeOptions> returningAttributes ) throws Exception
    {
        return directoryService.getOperationManager().search( new SearchOperationContext( this, dn, scope, filter, 
            aliasDerefMode, returningAttributes ) );
    }


    /* (non-Javadoc)
     * @see org.apache.directory.server.core.CoreSession#search(org.apache.directory.shared.ldap.name.LdapDN, org.apache.directory.shared.ldap.filter.SearchScope, org.apache.directory.shared.ldap.filter.ExprNode, org.apache.directory.shared.ldap.message.AliasDerefMode, java.util.Set, int, int)
     */
    public EntryFilteringCursor search( LdapDN dn, SearchScope scope, ExprNode filter, AliasDerefMode aliasDerefMode,
        Set<AttributeTypeOptions> returningAttributes, int sizeLimit, int timeLimit ) throws Exception
    {
        SearchOperationContext opContext = new SearchOperationContext( this, dn, scope, filter, 
            aliasDerefMode, returningAttributes );
        opContext.setSizeLimit( sizeLimit );
        opContext.setTimeLimit( timeLimit );
        return directoryService.getOperationManager().search( opContext );
    }


    /**
     * @param referralHandlingMode the referralHandlingMode to set
     */
    public void setReferralHandlingMode( ReferralHandlingMode referralHandlingMode )
    {
        this.referralHandlingMode = referralHandlingMode;
    }


    /**
     * @return the referralHandlingMode
     */
    public ReferralHandlingMode getReferralHandlingMode()
    {
        return referralHandlingMode;
    }
}
