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
package org.apache.directory.server.kerberos.protocol;


import org.apache.directory.server.kerberos.shared.io.decoder.KdcRequestDecoder;
import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;


/**
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class KerberosTcpDecoder extends CumulativeProtocolDecoder
{
    private KdcRequestDecoder decoder = new KdcRequestDecoder();


    protected boolean doDecode( IoSession session, ByteBuffer in, ProtocolDecoderOutput out ) throws Exception
    {
        if ( in.remaining() < 4 )
        {
            return false;
        }

        int recordLength = in.getInt();

        if ( in.remaining() < recordLength )
        {
            in.rewind();
            return false;
        }

        out.write( decoder.decode( in.buf() ) );

        return true;
    }
}
