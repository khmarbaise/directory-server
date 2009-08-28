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
package org.apache.directory.server.schema;


import java.io.UnsupportedEncodingException; 

import javax.naming.NamingException;

import org.apache.directory.shared.ldap.entry.client.ClientBinaryValue;
import org.apache.directory.shared.ldap.name.NameComponentNormalizer;
import org.apache.directory.shared.ldap.schema.AttributeType;
import org.apache.directory.shared.ldap.schema.MatchingRule;
import org.apache.directory.shared.ldap.schema.Normalizer;
import org.apache.directory.shared.ldap.schema.normalizers.NoOpNormalizer;
import org.apache.directory.shared.ldap.schema.registries.AttributeTypeRegistry;
import org.apache.directory.shared.ldap.util.StringTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A DN Name component Normalizer which uses the bootstrap registries to find
 * the appropriate normalizer for the attribute of the name component with which
 * to normalize the name component value.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class ConcreteNameComponentNormalizer implements NameComponentNormalizer
{
    /** The LoggerFactory used by this Interceptor */
    private static Logger LOG = LoggerFactory.getLogger( ConcreteNameComponentNormalizer.class );

    /** the at registry used to dynamically resolve Normalizers */
    private final AttributeTypeRegistry attributeRegistry;
    

    /**
     * Creates a DN Name component Normalizer which uses the bootstrap
     * registries to find the appropriate normalizer for the attribute of the
     * name component with which to normalize the name component value.
     *
     * @param registry the at registry used to dynamically resolve Normalizers
     */
    public ConcreteNameComponentNormalizer( AttributeTypeRegistry registry )
    {
        this.attributeRegistry = registry;
    }

    
    private String unescape( String value )
    {
        char[] newVal = new char[value.length()];
        int escaped = 0;
        char high = 0;
        char low = 0;
        int pos = 0;
        
        for ( char c:value.toCharArray() )
        {
            switch ( escaped )
            {
                case 0 :
                    if ( c == '\\' )
                    {
                        escaped = 1;
                    }
                    else
                    {
                        newVal[pos++] = c;
                    }
                    
                    break;

                case 1 :
                    escaped++;
                    high = c;
                    break;
                    
                case 2 :
                    escaped=0;
                    low = c;
                    newVal[pos++] = (char)StringTools.getHexValue( high, low );
                    
            }
        }
        
        return new String( newVal, 0, pos );
    }

    /**
     * @see NameComponentNormalizer#normalizeByName(String, String)
     */
    public Object normalizeByName( String name, String value ) throws NamingException
    {
        AttributeType attributeType = attributeRegistry.lookup( name );
        
        if ( attributeType.getSyntax().isHumanReadable() )
        {
            return lookup( name ).normalize( value );
        }
        else
        {
            try
            {
                String unescaped = unescape( value );
                byte[] valBytes = unescaped.getBytes( "UTF-8" );
                
                return lookup( name ).normalize( new ClientBinaryValue( valBytes ) ); 
            }
            catch ( UnsupportedEncodingException uee )
            {
                String message = "The value stored in a non Human Readable attribute as a String should be convertible to a byte[]";
                LOG.error( message );
                throw new NamingException( message );
            }
        }
        
    }


    /**
     * @see NameComponentNormalizer#normalizeByName(String, String)
     */
    public Object normalizeByName( String name, byte[] value ) throws NamingException
    {
        AttributeType attributeType = attributeRegistry.lookup( name );
        
        if ( !attributeType.getSyntax().isHumanReadable() )
        {
            return lookup( name ).normalize( new ClientBinaryValue( value ) );
        }
        else
        {
            try
            {
                String valStr = new String( value, "UTF-8" );
                return lookup( name ).normalize( valStr ); 
            }
            catch ( UnsupportedEncodingException uee )
            {
                String message = "The value stored in an Human Readable attribute as a byte[] should be convertible to a String";
                LOG.error( message );
                throw new NamingException( message );
            }
        }
    }


    /**
     * @see NameComponentNormalizer#normalizeByOid(String, String)
     */
    public Object normalizeByOid( String oid, String value ) throws NamingException
    {
        return lookup( oid ).normalize( value );
    }


    /**
     * @see NameComponentNormalizer#normalizeByOid(String, String)
     */
    public Object normalizeByOid( String oid, byte[] value ) throws NamingException
    {
        return lookup( oid ).normalize( new ClientBinaryValue( value ) );
    }


    /**
     * Looks up the Normalizer to use for a name component using the attributeId
     * for the name component.  First the attribute is resolved, then its
     * equality matching rule is looked up.  The normalizer of that matching
     * rule is returned.
     *
     * @param id the name or oid of the attribute in the name component to
     * normalize the value of
     * @return the Normalizer to use for normalizing the value of the attribute
     * @throws NamingException if there are failures resolving the Normalizer
     */
    private Normalizer lookup( String id ) throws NamingException
    {
        AttributeType type = attributeRegistry.lookup( id );
        MatchingRule mrule = type.getEquality();
        
        if ( mrule == null )
        {
            return new NoOpNormalizer( id );
        }
        
        return type.getEquality().getNormalizer();
    }


    /**
     * @see NameComponentNormalizer#isDefined(String)
     */
    public boolean isDefined( String id )
    {
        return attributeRegistry.contains( id );
    }


    public String normalizeName( String attributeName ) throws NamingException
    {
        return attributeRegistry.getOid( attributeName );
    }
}
