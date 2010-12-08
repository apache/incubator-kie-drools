/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.repository.remoteapi;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;



public abstract class Response {
	public abstract void writeData(OutputStream out) throws IOException ;
	public Calendar lastModified;


	public static class Text extends Response {
		String data;

		public Text() {}

		public Text(String s) {
			this.data = s;
		}
		@Override
		public void writeData(OutputStream out) throws IOException {
			out.write(data.getBytes());
		}
	}

	public static class Binary extends Response {

		InputStream stream;



		@Override
		public void writeData(OutputStream out) throws IOException {
			try {
				InputStream in = stream;
				if (!(out instanceof BufferedOutputStream)) out = new BufferedOutputStream(out);
				if (!(in instanceof BufferedInputStream)) in = new BufferedInputStream(in);
	            final byte[] buf = new byte[1024];
	            int len = 0;
	            while ( (len = in.read( buf )) >= 0 ) {
	                out.write( buf,
	                           0,
	                           len );
	            }
			} finally {
	            out.flush();
	            out.close();
	            stream.close();
			}
		}

	}


}


