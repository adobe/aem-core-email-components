/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2022 Adobe
 ~
 ~ Licensed under the Apache License, Version 2.0 (the "License");
 ~ you may not use this file except in compliance with the License.
 ~ You may obtain a copy of the License at
 ~
 ~     http://www.apache.org/licenses/LICENSE-2.0
 ~
 ~ Unless required by applicable law or agreed to in writing, software
 ~ distributed under the License is distributed on an "AS IS" BASIS,
 ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ~ See the License for the specific language governing permissions and
 ~ limitations under the License.
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
package com.adobe.cq.email.core.components.internal.request;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Internal class for the request resolver wrapper object. Please ignore the
 * NOPMDs - this code has been taken from a sling package ;-)
 */
public class URI implements Cloneable, Comparable<URI>, Serializable {
	private static final Logger logger = LoggerFactory.getLogger(URI.class);

	private static final int HEX_0X20 = 0x20;
	private static final int HEX_0X7F = 0x7F;
	private static final int HEX_0X1F = 0x1F;
	private static final int HEX_0X_F = 0xF;
	private static final int BYTES_16 = 16;
	private static final int BYTES_256 = 256;

	/** Create an instance as an internal use */
	protected URI() {
		// do nothing
	}

	/**
	 * Construct a URI from a string with the given charset. The input string
	 * can be either in escaped or unescaped form.
	 *
	 * @param sss
	 *            URI character sequence
	 * @param escaped
	 *            <tt>true</tt> if URI character sequence is in escaped form.
	 *            <tt>false</tt> otherwise.
	 * @param charset
	 *            the charset string to do escape encoding, if required
	 * @throws com.adobe.cq.email.core.components.internal.request.URIException
	 *             If the URI cannot be created.
	 * @throws NullPointerException
	 *             if input string is <code>null</code>
	 * @see #getProtocolCharset
	 * @since 3.0
	 */
	public URI(String sss, boolean escaped, String charset) throws com.adobe.cq.email.core.components.internal.request.URIException, NullPointerException {
		protocolCharset = charset;
		parseUriReference(sss, escaped);
	}

	/**
	 * Construct a URI from a string with the given charset. The input string
	 * can be either in escaped or unescaped form.
	 *
	 * @param sss
	 *            URI character sequence
	 * @param escaped
	 *            <tt>true</tt> if URI character sequence is in escaped form.
	 *            <tt>false</tt> otherwise.
	 * @throws com.adobe.cq.email.core.components.internal.request.URIException
	 *             If the URI cannot be created.
	 * @throws NullPointerException
	 *             if input string is <code>null</code>
	 * @see #getProtocolCharset
	 * @since 3.0
	 */
	public URI(String sss, boolean escaped) throws com.adobe.cq.email.core.components.internal.request.URIException, NullPointerException {
		parseUriReference(sss, escaped);
	}

	// --------------------------------------------------- Instance Variables

	/** Version ID for serialization */
	static final long serialVersionUID = 604752400577948726L;

	private int hash = 0;
	private char[] aUri = null;
	private String protocolCharset = null;
	private static String defaultProtocolCharset = "UTF-8";
	private static String defaultDocumentCharset = null;
	private static String defaultDocumentCharsetByLocale = null;
	private static String defaultDocumentCharsetByPlatform = null;
	// Static initializer for defaultDocumentCharset
	static {
		Locale locale = Locale.getDefault();
		// in order to support backward compatiblity
		if (locale != null) {
			defaultDocumentCharsetByLocale = LocaleToCharsetMap.getCharset(locale);
			// set the default document charset
			defaultDocumentCharset = defaultDocumentCharsetByLocale;
		}
		// in order to support platform encoding
		try {
			defaultDocumentCharsetByPlatform = System.getProperty("file.encoding");
		} catch (SecurityException ignore) {
			logger.warn("Caught an error.");
		}
		if (defaultDocumentCharset == null) {
			// set the default document charset
			defaultDocumentCharset = defaultDocumentCharsetByPlatform;
		}
	}

	private char[] aScheme = null;
	private char[] aOpaque = null;
	private char[] aAuthority = null;
	private char[] aUserinfo = null;
	private char[] aHost = null;
	private int aPort = -1;
	private char[] aPath = null;
	private char[] aQuery = null;
	private char[] aFragment = null;

	// ---------------------- Generous characters for each component validation

	private static final BitSet PERCENT = new BitSet(BYTES_256);
	// Static initializer for percent
	static {
		PERCENT.set('%');
	}

	private static final BitSet DIGIT = new BitSet(BYTES_256);
	// Static initializer for digit
	static {
		for (int i = '0'; i <= '9'; i++) {
			DIGIT.set(i);
		}
	}

	private static final BitSet ALPHA = new BitSet(BYTES_256);
	// Static initializer for alpha
	static {
		for (int i = 'a'; i <= 'z'; i++) {
			ALPHA.set(i);
		}
		for (int i = 'A'; i <= 'Z'; i++) {
			ALPHA.set(i);
		}
	}

	private static final BitSet ALPHANUM = new BitSet(BYTES_256);
	// Static initializer for alphanum
	static {
		ALPHANUM.or(ALPHA);
		ALPHANUM.or(DIGIT);
	}

	private static final BitSet HEX = new BitSet(BYTES_256);
	// Static initializer for hex
	static {
		HEX.or(DIGIT);
		for (int i = 'a'; i <= 'f'; i++) {
			HEX.set(i);
		}
		for (int i = 'A'; i <= 'F'; i++) {
			HEX.set(i);
		}
	}

	private static final BitSet ESCAPED = new BitSet(BYTES_256);
	// Static initializer for escaped
	static {
		ESCAPED.or(PERCENT);
		ESCAPED.or(HEX);
	}

	private static final BitSet MARK = new BitSet(BYTES_256);
	// Static initializer for mark
	static {
		MARK.set('-');
		MARK.set('_');
		MARK.set('.');
		MARK.set('!');
		MARK.set('~');
		MARK.set('*');
		MARK.set('\'');
		MARK.set('(');
		MARK.set(')');
	}

	private static final BitSet UNRESERVED = new BitSet(BYTES_256);
	// Static initializer for unreserved
	static {
		UNRESERVED.or(ALPHANUM);
		UNRESERVED.or(MARK);
	}

	private static final BitSet RESERVED = new BitSet(BYTES_256);
	// Static initializer for reserved
	static {
		RESERVED.set(';');
		RESERVED.set('/');
		RESERVED.set('?');
		RESERVED.set(':');
		RESERVED.set('@');
		RESERVED.set('&');
		RESERVED.set('=');
		RESERVED.set('+');
		RESERVED.set('$');
		RESERVED.set(',');
	}

	private static final BitSet URIC = new BitSet(BYTES_256);
	// Static initializer for uric
	static {
		URIC.or(RESERVED);
		URIC.or(UNRESERVED);
		URIC.or(ESCAPED);
	}

	private static final BitSet FRAGMENT = URIC;

	private static final BitSet QUERY = URIC;

	private static final BitSet PCHAR = new BitSet(BYTES_256);
	// Static initializer for pchar
	static {
		PCHAR.or(UNRESERVED);
		PCHAR.or(ESCAPED);
		PCHAR.set(':');
		PCHAR.set('@');
		PCHAR.set('&');
		PCHAR.set('=');
		PCHAR.set('+');
		PCHAR.set('$');
		PCHAR.set(',');
	}

	private static final BitSet PARAM = PCHAR;

	private static final BitSet SEGMENT = new BitSet(BYTES_256);
	// Static initializer for segment
	static {
		SEGMENT.or(PCHAR);
		SEGMENT.set(';');
		SEGMENT.or(PARAM);
	}

	private static final BitSet PATH_SEGMENTS = new BitSet(BYTES_256);
	// Static initializer for path_segments
	static {
		PATH_SEGMENTS.set('/');
		PATH_SEGMENTS.or(SEGMENT);
	}

	private static final BitSet ABS_PATH = new BitSet(BYTES_256);
	// Static initializer for abs_path
	static {
		ABS_PATH.set('/');
		ABS_PATH.or(PATH_SEGMENTS);
	}

	private static final BitSet URIC_NO_SLASH = new BitSet(BYTES_256);
	// Static initializer for uric_no_slash
	static {
		URIC_NO_SLASH.or(UNRESERVED);
		URIC_NO_SLASH.or(ESCAPED);
		URIC_NO_SLASH.set(';');
		URIC_NO_SLASH.set('?');
		URIC_NO_SLASH.set(';');
		URIC_NO_SLASH.set('@');
		URIC_NO_SLASH.set('&');
		URIC_NO_SLASH.set('=');
		URIC_NO_SLASH.set('+');
		URIC_NO_SLASH.set('$');
		URIC_NO_SLASH.set(',');
	}

	private static final BitSet OPAQUE_PART = new BitSet(BYTES_256);
	// Static initializer for opaque_part
	static {
		// it's generous. because first character must not include a slash
		OPAQUE_PART.or(URIC_NO_SLASH);
		OPAQUE_PART.or(URIC);
	}

	private static final BitSet PATH = new BitSet(BYTES_256);
	// Static initializer for path
	static {
		PATH.or(ABS_PATH);
		PATH.or(OPAQUE_PART);
	}

	private static final BitSet PORT = DIGIT;

	private static final BitSet IPV4ADDRESS = new BitSet(BYTES_256);
	// Static initializer for IPv4address
	static {
		IPV4ADDRESS.or(DIGIT);
		IPV4ADDRESS.set('.');
	}

	private static final BitSet IPV6ADDRESS = new BitSet(BYTES_256);
	// Static initializer for IPv6address reference
	static {
		IPV6ADDRESS.or(HEX); // hexpart
		IPV6ADDRESS.set(':');
		IPV6ADDRESS.or(IPV4ADDRESS);
	}

	private static final BitSet IPV6REFERENCE = new BitSet(BYTES_256);
	// Static initializer for IPv6reference
	static {
		IPV6REFERENCE.set('[');
		IPV6REFERENCE.or(IPV6ADDRESS);
		IPV6REFERENCE.set(']');
	}

	private static final BitSet TOPLABEL = new BitSet(BYTES_256);
	// Static initializer for toplabel
	static {
		TOPLABEL.or(ALPHANUM);
		TOPLABEL.set('-');
	}

	private static final BitSet HOSTNAME = new BitSet(BYTES_256);
	// Static initializer for hostname
	static {
		HOSTNAME.or(TOPLABEL);
		// hostname.or(domainlabel);
		HOSTNAME.set('.');
	}

	private static final BitSet HOST = new BitSet(BYTES_256);
	// Static initializer for host
	static {
		HOST.or(HOSTNAME);
		// host.or(IPv4address);
		HOST.or(IPV6REFERENCE); // IPv4address
	}

	private static final BitSet HOSTPORT = new BitSet(BYTES_256);
	// Static initializer for hostport
	static {
		HOSTPORT.or(HOST);
		HOSTPORT.set(':');
		HOSTPORT.or(PORT);
	}

	private static final BitSet USERINFO = new BitSet(BYTES_256);
	// Static initializer for userinfo
	static {
		USERINFO.or(UNRESERVED);
		USERINFO.or(ESCAPED);
		USERINFO.set(';');
		USERINFO.set(':');
		USERINFO.set('&');
		USERINFO.set('=');
		USERINFO.set('+');
		USERINFO.set('$');
		USERINFO.set(',');
	}

	/**
	 * BitSet for within the userinfo component like user and password.
	 */
	private static final BitSet WI_USERINFO = new BitSet(BYTES_256);
	// Static initializer for within_userinfo
	static {
		WI_USERINFO.or(USERINFO);
		WI_USERINFO.clear(';'); // reserved within authority
		WI_USERINFO.clear(':');
		WI_USERINFO.clear('@');
		WI_USERINFO.clear('?');
		WI_USERINFO.clear('/');
	}

	private static final BitSet SERVER = new BitSet(BYTES_256);
	// Static initializer for server
	static {
		SERVER.or(USERINFO);
		SERVER.set('@');
		SERVER.or(HOSTPORT);
	}

	private static final BitSet REG_NAME = new BitSet(BYTES_256);
	// Static initializer for reg_name
	static {
		REG_NAME.or(UNRESERVED);
		REG_NAME.or(ESCAPED);
		REG_NAME.set('$');
		REG_NAME.set(',');
		REG_NAME.set(';');
		REG_NAME.set(':');
		REG_NAME.set('@');
		REG_NAME.set('&');
		REG_NAME.set('=');
		REG_NAME.set('+');
	}

	private static final BitSet AUTHORITY = new BitSet(BYTES_256);
	// Static initializer for authority
	static {
		AUTHORITY.or(SERVER);
		AUTHORITY.or(REG_NAME);
	}

	private static final BitSet SCHEME = new BitSet(BYTES_256);
	// Static initializer for scheme
	static {
		SCHEME.or(ALPHA);
		SCHEME.or(DIGIT);
		SCHEME.set('+');
		SCHEME.set('-');
		SCHEME.set('.');
	}

	private static final BitSet REL_SEGMENT = new BitSet(BYTES_256);
	// Static initializer for rel_segment
	static {
		REL_SEGMENT.or(UNRESERVED);
		REL_SEGMENT.or(ESCAPED);
		REL_SEGMENT.set(';');
		REL_SEGMENT.set('@');
		REL_SEGMENT.set('&');
		REL_SEGMENT.set('=');
		REL_SEGMENT.set('+');
		REL_SEGMENT.set('$');
		REL_SEGMENT.set(',');
	}

	private static final BitSet REL_PATH = new BitSet(BYTES_256);
	// Static initializer for rel_path
	static {
		REL_PATH.or(REL_SEGMENT);
		REL_PATH.or(ABS_PATH);
	}

	private static final BitSet NET_PATH = new BitSet(BYTES_256);
	// Static initializer for net_path
	static {
		NET_PATH.set('/');
		NET_PATH.or(AUTHORITY);
		NET_PATH.or(ABS_PATH);
	}

	private static final BitSet HIER_PART = new BitSet(BYTES_256);
	// Static initializer for hier_part
	static {
		HIER_PART.or(NET_PATH);
		HIER_PART.or(ABS_PATH);
		// hier_part.set('?'); aleady included
		HIER_PART.or(QUERY);
	}

	private static final BitSet RELATIVE_URI = new BitSet(BYTES_256);
	// Static initializer for relativeURI
	static {
		RELATIVE_URI.or(NET_PATH);
		RELATIVE_URI.or(ABS_PATH);
		RELATIVE_URI.or(REL_PATH);
		// relativeURI.set('?'); aleady included
		RELATIVE_URI.or(QUERY);
	}

	private static final BitSet ABSOLUTE_URI = new BitSet(BYTES_256);
	// Static initializer for absoluteURI
	static {
		ABSOLUTE_URI.or(SCHEME);
		ABSOLUTE_URI.set(':');
		ABSOLUTE_URI.or(HIER_PART);
		ABSOLUTE_URI.or(OPAQUE_PART);
	}

	private static final BitSet URI_REFERENCE = new BitSet(BYTES_256);
	// Static initializer for URI_reference
	static {
		URI_REFERENCE.or(ABSOLUTE_URI);
		URI_REFERENCE.or(RELATIVE_URI);
		URI_REFERENCE.set('#');
		URI_REFERENCE.or(FRAGMENT);
	}

	private static final BitSet CONTROL = new BitSet(BYTES_256);
	// Static initializer for control
	static {
		for (int i = 0; i <= HEX_0X1F; i++) {
			CONTROL.set(i);
		}
		CONTROL.set(HEX_0X7F);
	}

	/**
	 * BitSet for space.
	 */
	private static final BitSet SPACE = new BitSet(BYTES_256);
	// Static initializer for space
	static {
		SPACE.set(HEX_0X20);
	}

	/**
	 * BitSet for delims.
	 */
	public static final BitSet DELIMS = new BitSet(BYTES_256);
	// Static initializer for delims
	static {
		DELIMS.set('<');
		DELIMS.set('>');
		DELIMS.set('#');
		DELIMS.set('%');
		DELIMS.set('"');
	}

	/**
	 * BitSet for unwise.
	 */
	private static final BitSet UNWISE = new BitSet(BYTES_256);
	// Static initializer for unwise
	static {
		UNWISE.set('{');
		UNWISE.set('}');
		UNWISE.set('|');
		UNWISE.set('\\');
		UNWISE.set('^');
		UNWISE.set('[');
		UNWISE.set(']');
		UNWISE.set('`');
	}

	/**
	 * Disallowed rel_path before escaping.
	 */
	private static final BitSet DISALLOWED_REL_PATH = new BitSet(BYTES_256);
	// Static initializer for disallowed_rel_path
	static {
		DISALLOWED_REL_PATH.or(URIC);
		DISALLOWED_REL_PATH.andNot(REL_PATH);
	}

	/**
	 * Disallowed opaque_part before escaping.
	 */
	private static final BitSet DISALLOWED_OPAQUE_PART = new BitSet(BYTES_256);
	// Static initializer for disallowed_opaque_part
	static {
		DISALLOWED_OPAQUE_PART.or(URIC);
		DISALLOWED_OPAQUE_PART.andNot(OPAQUE_PART);
	}

	// ----------------------- Characters allowed within and for each component

	/**
	 * Those characters that are allowed for the authority component.
	 */
	private static final BitSet ALLOWED_AUTHORITY = new BitSet(BYTES_256);
	// Static initializer for allowed_authority
	static {
		ALLOWED_AUTHORITY.or(AUTHORITY);
		ALLOWED_AUTHORITY.clear('%');
	}

	private static final BitSet ALLOWED_OPAQUE_PART = new BitSet(BYTES_256);
	// Static initializer for allowed_opaque_part
	static {
		ALLOWED_OPAQUE_PART.or(OPAQUE_PART);
		ALLOWED_OPAQUE_PART.clear('%');
	}

	private static final BitSet ALLOWED_REG_NAME = new BitSet(BYTES_256);
	static {
		ALLOWED_REG_NAME.or(REG_NAME);
		ALLOWED_REG_NAME.clear('%');
	}

	private static final BitSet ALLOWED_USERINFO = new BitSet(BYTES_256);
	static {
		ALLOWED_USERINFO.or(USERINFO);
		ALLOWED_USERINFO.clear('%');
	}

	private static final BitSet ALLOWED_WI_USERINFO = new BitSet(BYTES_256);
	static {
		ALLOWED_WI_USERINFO.or(WI_USERINFO);
		ALLOWED_WI_USERINFO.clear('%');
	}

	private static final BitSet ALLOWED_IPV6_REF = new BitSet(BYTES_256);
	static {
		ALLOWED_IPV6_REF.or(IPV6REFERENCE);
		ALLOWED_IPV6_REF.clear('[');
		ALLOWED_IPV6_REF.clear(']');
	}

	private static final BitSet ALLOWED_HOST = new BitSet(BYTES_256);
	static {
		ALLOWED_HOST.or(HOSTNAME);
		ALLOWED_HOST.or(ALLOWED_IPV6_REF);
	}

	private static final BitSet ALLOWED_WI_AUTH = new BitSet(BYTES_256);
	static {
		ALLOWED_WI_AUTH.or(SERVER);
		ALLOWED_WI_AUTH.or(REG_NAME);
		ALLOWED_WI_AUTH.clear(';');
		ALLOWED_WI_AUTH.clear(':');
		ALLOWED_WI_AUTH.clear('@');
		ALLOWED_WI_AUTH.clear('?');
		ALLOWED_WI_AUTH.clear('/');
	}

	private static final BitSet ALLOWED_ABS_PATH = new BitSet(BYTES_256);
	static {
		ALLOWED_ABS_PATH.or(ABS_PATH);
		ALLOWED_ABS_PATH.andNot(PERCENT);
		ALLOWED_ABS_PATH.clear('+');
	}

	private static final BitSet ALLOWED_REL_PATH = new BitSet(BYTES_256);
	static {
		ALLOWED_REL_PATH.or(REL_PATH);
		ALLOWED_REL_PATH.clear('%');
		ALLOWED_REL_PATH.clear('+');
	}

	private static final BitSet ALLOWED_WI_PATH = new BitSet(BYTES_256);
	static {
		ALLOWED_WI_PATH.or(ABS_PATH);
		ALLOWED_WI_PATH.clear('/');
		ALLOWED_WI_PATH.clear(';');
		ALLOWED_WI_PATH.clear('=');
		ALLOWED_WI_PATH.clear('?');
	}

	private static final BitSet ALLOWED_QUERY = new BitSet(BYTES_256);
	static {
		ALLOWED_QUERY.or(URIC);
		ALLOWED_QUERY.clear('%');
	}

	private static final BitSet ALLOWED_WI_QUERY = new BitSet(BYTES_256);
	static {
		ALLOWED_WI_QUERY.or(ALLOWED_QUERY);
		ALLOWED_WI_QUERY.andNot(RESERVED); // excluded 'reserved'
	}

	private static final BitSet ALLOWED_FRAGMENT = new BitSet(BYTES_256);
	static {
		ALLOWED_FRAGMENT.or(URIC);
		ALLOWED_FRAGMENT.clear('%');
	}

	private boolean ishierpart;
	private boolean isopaquepart;
	private boolean isnetpath;
	private boolean isabspath;
	private boolean isrelpath;
	private boolean isregname;
	private boolean isServer; // = _has_server
	private boolean ishostname;
	private boolean isIPv4address;
	private boolean isIPv6reference;

	/**
	 * bla
	 *
	 * @param original
	 *            bla
	 * @param allowed
	 *            bla
	 * @param charset
	 *            bla
	 * @return bla
	 * @throws com.adobe.cq.email.core.components.internal.request.URIException
	 *             bla
	 */
	private static char[] encode(String original, BitSet allowed, String charset) throws com.adobe.cq.email.core.components.internal.request.URIException {
		if (original == null) {
			throw new IllegalArgumentException("Original string may not be null");
		}
		if (allowed == null) {
			throw new IllegalArgumentException("Allowed bitset may not be null");
		}
		byte[] rawdata = encodeUrl(allowed, getBytes(original, charset));
		return getAsciiString(rawdata).toCharArray();
	}

	/**
	 * bla
	 *
	 * @param component
	 *            bla
	 * @param charset
	 *            bla
	 * @return bla
	 * @throws com.adobe.cq.email.core.components.internal.request.URIException
	 *             bla
	 */
	private static String decode(char[] component, String charset) throws com.adobe.cq.email.core.components.internal.request.URIException {
		if (component == null) {
			throw new IllegalArgumentException("Component array of chars may not be null");
		}
		return decode(new String(component), charset);
	}

	/**
	 * bla
	 *
	 * @param component
	 *            bla
	 * @param charset
	 *            bla
	 * @return bla
	 * @throws com.adobe.cq.email.core.components.internal.request.URIException
	 *             bla
	 */
	public static String decode(String component, String charset) throws com.adobe.cq.email.core.components.internal.request.URIException {
		if (component == null) {
			throw new IllegalArgumentException("Component array of chars may not be null");
		}
		byte[] rawdata = decodeUrl(getAsciiBytes(component));
		return getString(rawdata, charset);
	}

	/**
	 * bla
	 *
	 * @param component
	 *            bla
	 * @param disallowed
	 *            bla
	 * @return bla
	 */
	public boolean prevalidate(String component, BitSet disallowed) {
		if (component == null) {
			return false; // undefined
		}
		char[] target = component.toCharArray();
		for (char element : target) {
			if (disallowed.get(element)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * bla
	 *
	 * @param component
	 *            bla
	 * @param generous
	 *            bla
	 * @return bla
	 */
	public boolean validate(char[] component, BitSet generous) {
		return validate(component, 0, -1, generous);
	}

	/**
	 * bla
	 *
	 * @param component
	 *            bla
	 * @param soffset
	 *            bla
	 * @param eoffset
	 *            bla
	 * @param generous
	 *            bla
	 * @return bla
	 */
	private boolean validate(char[] component, int soffset, int eoffset, BitSet generous) {
		int aeoffset = eoffset;
		// validate each component by generous characters
		if (aeoffset == -1) {
			aeoffset = component.length - 1;
		}
		for (int i = soffset; i <= aeoffset; i++) {
			if (!generous.get(component[i])) {
				return false;
			}
		}
		return true;
	}

	/**
	 * bla
	 *
	 * @param original
	 *            bla
	 * @param escaped
	 *            bla
	 * @throws com.adobe.cq.email.core.components.internal.request.URIException
	 *             bla
	 */
	private void parseUriReference(String original, boolean escaped) throws com.adobe.cq.email.core.components.internal.request.URIException {
		if (original == null) {
			throw new com.adobe.cq.email.core.components.internal.request.URIException("URI-Reference required");
		}
		String tmp = original.trim();
		int length = tmp.length();
		if (length > 0) {
			char[] firstDelimiter = { tmp.charAt(0) };
			if (validate(firstDelimiter, DELIMS)) {
				if (length >= 2) {
					char[] lastDelimiter = { tmp.charAt(length - 1) };
					if (validate(lastDelimiter, DELIMS)) {
						tmp = tmp.substring(1, length - 1);
						length = length - 2;
					}
				}
			}
		}
		int from = 0;
		boolean isStartedFromPath = false;
		int atColon = tmp.indexOf(':');
		int atSlash = tmp.indexOf('/');
		if ((atColon <= 0 && !tmp.startsWith("//")) || (atSlash >= 0 && atSlash < atColon)) {
			isStartedFromPath = true;
		}
		int atat = indexFirstOf(tmp, isStartedFromPath ? "/?#" : ":/?#", from);
		if (atat == -1) {
			atat = 0;
		}
		if (atat > 0 && atat < length && tmp.charAt(atat) == ':') {
			char[] target = tmp.substring(0, atat).toLowerCase().toCharArray();
			if (validate(target, SCHEME)) {
				aScheme = target;
			} else {
				throw new com.adobe.cq.email.core.components.internal.request.URIException("incorrect scheme");
			}
			from = ++atat;
		}
		isnetpath = false;
		isabspath = false;
		isrelpath = false;
		ishierpart = false;
		if (0 <= atat && atat < length && tmp.charAt(atat) == '/') {
			ishierpart = true;
			if (atat + 2 < length && tmp.charAt(atat + 1) == '/' && !isStartedFromPath) {
				int next = indexFirstOf(tmp, "/?#", atat + 2);
				if (next == -1) {
					next = (tmp.substring(atat + 2).length() == 0) ? (atat + 2) : tmp.length();
				}
				parseAuthority(tmp.substring(atat + 2, next), escaped);
				from = next;
				atat = next;
				isnetpath = true;
			}
			if (from == atat) {
				isabspath = true;
			}
		}
		if (from < length) {
			int next = indexFirstOf(tmp, "?#", from);
			if (next == -1) {
				next = tmp.length();
			}
			if (!isabspath) {
				if ((!escaped && prevalidate(tmp.substring(from, next), DISALLOWED_REL_PATH)) || (escaped && validate(tmp.substring(from, next).toCharArray(), REL_PATH))) {
					isrelpath = true;
				} else if ((!escaped && prevalidate(tmp.substring(from, next), DISALLOWED_OPAQUE_PART)) || (escaped && validate(tmp.substring(from, next).toCharArray(), OPAQUE_PART))) {
					isopaquepart = true;
				} else {
					aPath = null;
				}
			}
			String sss = tmp.substring(from, next);
			if (escaped) {
				setRawPath(sss);
			} else {
				setPath(sss);
			}
			atat = next;
		}
		String charset = getProtocolCharset();
		if (0 <= atat && atat + 1 < length && tmp.charAt(atat) == '?') {
			int next = tmp.indexOf('#', atat + 1);
			if (next == -1) {
				next = tmp.length();
			}
			if (escaped) {
				aQuery = tmp.substring(atat + 1, next).toCharArray();
				if (!validate(aQuery, URIC)) {
					throw new com.adobe.cq.email.core.components.internal.request.URIException("Invalid query");
				}
			} else {
				aQuery = encode(tmp.substring(atat + 1, next), ALLOWED_QUERY, charset);
			}
			atat = next;
		}
		if (0 <= atat && atat + 1 <= length && tmp.charAt(atat) == '#') {
			if (atat + 1 == length) { // empty fragment
				aFragment = "".toCharArray();
			} else {
				aFragment = escaped ? tmp.substring(atat + 1).toCharArray() : encode(tmp.substring(atat + 1), ALLOWED_FRAGMENT, charset);
			}
		}
		setURI();
	}

	/**
	 * bla
	 *
	 * @param sss
	 *            bla
	 * @param delims
	 *            bla
	 * @param offset
	 *            bla
	 * @return bla
	 */
	protected int indexFirstOf(String sss, String delims, int offset) {
		if (sss == null || sss.length() == 0) {
			return -1;
		}
		if (delims == null || delims.length() == 0) {
			return -1;
		}
		if (offset < 0) {
			offset = 0;
		} else if (offset > sss.length()) {
			return -1;
		}
		int min = sss.length();
		char[] delim = delims.toCharArray();
		for (char element : delim) {
			int at = sss.indexOf(element, offset);
			if (at >= 0 && at < min) {
				min = at;
			}
		}
		return (min == sss.length()) ? -1 : min;
	}

	/**
	 * bla
	 *
	 * @param sss
	 *            bla
	 * @param delim
	 *            bla
	 * @return bla
	 */
	public int indexFirstOf(char[] sss, char delim) {
		return indexFirstOf(sss, delim, 0);
	}

	/**
	 * bla
	 *
	 * @param sss
	 *            bla
	 * @param delim
	 *            bla
	 * @param offset
	 *            bla
	 * @return bla
	 */
	private int indexFirstOf(char[] sss, char delim, int offset) {
		if (sss == null || sss.length == 0) {
			return -1;
		}
		// check boundaries
		if (offset < 0) {
			offset = 0;
		} else if (offset > sss.length) {
			return -1;
		}
		for (int i = offset; i < sss.length; i++) {
			if (sss[i] == delim) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * bla
	 *
	 * @param original
	 *            bla
	 * @param escaped
	 *            bla
	 * @throws com.adobe.cq.email.core.components.internal.request.URIException
	 *             bla
	 */
	private void parseAuthority(String original, boolean escaped) throws com.adobe.cq.email.core.components.internal.request.URIException {
		isregname = false;
		isServer = false;
		ishostname = false;
		isIPv4address = false;
		isIPv6reference = false;

		String charset = getProtocolCharset();

		boolean hasPort = true;
		int from = 0;
		int next = original.indexOf('@');
		if (next != -1) { // neither -1 and 0
			aUserinfo = escaped ? original.substring(0, next).toCharArray() : encode(original.substring(0, next), ALLOWED_USERINFO, charset);
			from = next + 1;
		}
		next = original.indexOf('[', from);
		if (next >= from) {
			next = original.indexOf(']', from);
			if (next == -1) {
				throw new com.adobe.cq.email.core.components.internal.request.URIException(com.adobe.cq.email.core.components.internal.request.URIException.PARSING, "IPv6reference");
			}
			next++;
			aHost = escaped ? original.substring(from, next).toCharArray() : encode(original.substring(from, next), ALLOWED_IPV6_REF, charset);
			isIPv6reference = true;
		} else { // only for !_is_IPv6reference
			next = original.indexOf(':', from);
			if (next == -1) {
				next = original.length();
				hasPort = false;
			}
			aHost = original.substring(from, next).toCharArray();
			if (validate(aHost, IPV4ADDRESS)) {
				isIPv4address = true;
			} else if (validate(aHost, HOSTNAME)) {
				ishostname = true;
			} else {
				isregname = true;
			}
		}
		if (isregname) {
			isServer = false;
			ishostname = false;
			isIPv4address = false;
			isIPv6reference = false;
			if (escaped) {
				aAuthority = original.toCharArray();
				if (!validate(aAuthority, REG_NAME)) {
					throw new com.adobe.cq.email.core.components.internal.request.URIException("Invalid authority");
				}
			} else {
				aAuthority = encode(original, ALLOWED_REG_NAME, charset);
			}
		} else {
			if (original.length() - 1 > next && hasPort && original.charAt(next) == ':') { // not empty
				from = next + 1;
				try {
					aPort = Integer.parseInt(original.substring(from));
				} catch (NumberFormatException error) {
					throw new com.adobe.cq.email.core.components.internal.request.URIException(com.adobe.cq.email.core.components.internal.request.URIException.PARSING, "invalid port number");
				}
			}
			StringBuilder buf = new StringBuilder();
			if (aUserinfo != null) { // has_userinfo
				buf.append(aUserinfo);
				buf.append('@');
			}
			if (aHost != null) {
				buf.append(aHost);
				if (aPort != -1) {
					buf.append(':');
					buf.append(aPort);
				}
			}
			aAuthority = buf.toString().toCharArray();
			isServer = true;
		}
	}

	/**
	 * bla
	 */
	private void setURI() {
		// set _uri
		StringBuilder buf = new StringBuilder();
		// ^(([^:/?#]+):)?(//([^/?#]*))?([^?#]*)(\?([^#]*))?(#(.*))?
		if (aScheme != null) {
			buf.append(aScheme);
			buf.append(':');
		}
		if (isnetpath) {
			buf.append("//");
			if (aAuthority != null) { // has_authority
				buf.append(aAuthority);
			}
		}
		if (aOpaque != null && isopaquepart) {
			buf.append(aOpaque);
		} else if (aPath != null) {
			// _is_hier_part or _is_relativeURI
			if (aPath.length != 0) {
				buf.append(aPath);
			}
		}
		if (aQuery != null) { // has_query
			buf.append('?');
			buf.append(aQuery);
		}
		// ignore the fragment identifier
		aUri = buf.toString().toCharArray();
		hash = 0;
	}

	/**
	 * Get the scheme.
	 *
	 * @return the scheme null if undefined scheme
	 */
	public String getScheme() {
		return (aScheme == null) ? null : new String(aScheme);
	}

	/**
	 * Get the host.
	 * <p>
	 * <blockquote>
	 *
	 * <pre>
	 * host = hostname | IPv4address | IPv6reference
	 * </pre>
	 *
	 * </blockquote>
	 * <p>
	 *
	 * @return the host
	 * @throws com.adobe.cq.email.core.components.internal.request.URIException
	 *             If {@link #decode} fails
	 */
	public String getHost() throws com.adobe.cq.email.core.components.internal.request.URIException {
		if (aHost != null) {
			return decode(aHost, getProtocolCharset());
		}
		return null;
	}

	// --------------------------------------------------------------- The port

	/**
	 * Get the port. In order to get the specfic default port, the specific
	 * protocol-supported class extended from the URI class should be used. It
	 * has the server-based naming authority.
	 *
	 * @return the port if -1, it has the default port for the scheme or the
	 *         server-based naming authority is not supported in the specific
	 *         URI.
	 */
	public int getPort() {
		return aPort;
	}

	/**
	 * Get the path.
	 * <p>
	 * <blockquote>
	 *
	 * <pre>
	 *   path          = [ abs_path | opaque_part ]
	 * </pre>
	 *
	 * </blockquote>
	 * <p>
	 *
	 * @return the path string
	 * @throws com.adobe.cq.email.core.components.internal.request.URIException
	 *             If {@link #decode} fails.
	 * @see #decode
	 */
	public String getPath() throws com.adobe.cq.email.core.components.internal.request.URIException {
		char[] path = getRawPath();
		return (path == null) ? null : decode(path, getProtocolCharset());
	}

	private String getProtocolCharset() {
		return (protocolCharset != null) ? protocolCharset : defaultProtocolCharset;
	}

	/**
	 * bla
	 *
	 * @param escapedPath
	 *            bla
	 * @throws com.adobe.cq.email.core.components.internal.request.URIException
	 *             bla
	 */
	private void setRawPath(String escapedPath) throws com.adobe.cq.email.core.components.internal.request.URIException {
        char[] escapedPathChars = escapedPath.toCharArray();
		if (escapedPathChars.length == 0) {
			aPath = escapedPathChars;
			aOpaque = escapedPathChars;
			setURI();
			return;
		}
        escapedPathChars = removeFragmentIdentifier(escapedPathChars);
		if (isnetpath || isabspath) {
			if (escapedPathChars[0] != '/') {
				throw new com.adobe.cq.email.core.components.internal.request.URIException(com.adobe.cq.email.core.components.internal.request.URIException.PARSING, "not absolute path");
			}
			if (!validate(escapedPathChars, ABS_PATH)) {
				throw new com.adobe.cq.email.core.components.internal.request.URIException(com.adobe.cq.email.core.components.internal.request.URIException.ESCAPING, "escaped absolute path not valid");
			}
			aPath = escapedPathChars;
		} else if (isrelpath) {
			int at = indexFirstOf(escapedPathChars, '/');
			if (at == 0) {
				throw new com.adobe.cq.email.core.components.internal.request.URIException(com.adobe.cq.email.core.components.internal.request.URIException.PARSING, "incorrect path");
			}
			if ((at > 0 && !validate(escapedPathChars, 0, at - 1, REL_SEGMENT) && !validate(escapedPathChars, at, -1, ABS_PATH)) || (at < 0 && !validate(escapedPathChars, 0, -1, REL_SEGMENT))) {

				throw new com.adobe.cq.email.core.components.internal.request.URIException(com.adobe.cq.email.core.components.internal.request.URIException.ESCAPING, "escaped relative path not valid");
			}
			aPath = escapedPathChars;
		} else if (isopaquepart) {
			if (!URIC_NO_SLASH.get(escapedPathChars[0]) && !validate(escapedPathChars, 1, -1, URIC)) {
				throw new com.adobe.cq.email.core.components.internal.request.URIException(com.adobe.cq.email.core.components.internal.request.URIException.ESCAPING, "escaped opaque part not valid");
			}
			aOpaque = escapedPathChars;
		} else {
			throw new com.adobe.cq.email.core.components.internal.request.URIException(com.adobe.cq.email.core.components.internal.request.URIException.PARSING, "incorrect path");
		}
		setURI();
	}

	/**
	 * bla
	 *
	 * @param path
	 *            bla
	 * @throws com.adobe.cq.email.core.components.internal.request.URIException
	 *             bla
	 */
	private void setPath(String path) throws com.adobe.cq.email.core.components.internal.request.URIException {
		if (path == null || path.length() == 0) {
			aOpaque = (path == null) ? null : path.toCharArray();
			aPath = aOpaque;
			setURI();
			return;
		}
		String charset = getProtocolCharset();
		if (isnetpath || isabspath) {
			aPath = encode(path, ALLOWED_ABS_PATH, charset);
		} else if (isrelpath) {
			StringBuilder buff = new StringBuilder(path.length());
			int at = path.indexOf('/');
			if (at == 0) { // never 0
				throw new com.adobe.cq.email.core.components.internal.request.URIException(com.adobe.cq.email.core.components.internal.request.URIException.PARSING, "incorrect relative path");
			}
			if (at > 0) {
				buff.append(encode(path.substring(0, at), ALLOWED_REL_PATH, charset));
				buff.append(encode(path.substring(at), ALLOWED_ABS_PATH, charset));
			} else {
				buff.append(encode(path, ALLOWED_REL_PATH, charset));
			}
			aPath = buff.toString().toCharArray();
		} else if (isopaquepart) {
			StringBuilder buf = new StringBuilder();
			buf.insert(0, encode(path.substring(0, 1), URIC_NO_SLASH, charset));
			buf.insert(1, encode(path.substring(1), URIC, charset));
			aOpaque = buf.toString().toCharArray();
		} else {
			throw new com.adobe.cq.email.core.components.internal.request.URIException(com.adobe.cq.email.core.components.internal.request.URIException.PARSING, "incorrect path");
		}
		setURI();
	}

	private char[] getRawPath() {
		return isopaquepart ? aOpaque : aPath;
	}

	/**
	 * bla
	 *
	 * @param component
	 *            bla
	 * @return bla
	 */
	private char[] removeFragmentIdentifier(char[] component) {
		if (component == null) {
			return null;
		}
		int lastIndex = new String(component).indexOf('#');
		if (lastIndex != -1) {
			component = new String(component).substring(0, lastIndex).toCharArray();
		}
		return component;
	}

	/**
	 * Bla
	 *
	 * @param first
	 *            bla
	 * @param second
	 *            bla
	 * @return bla
	 */
	public boolean equals(char[] first, char[] second) {

		if (first == null && second == null) {
			return true;
		}
		if (first == null || second == null) {
			return false;
		}
		if (first.length != second.length) {
			return false;
		}
		for (int i = 0; i < first.length; i++) {
			if (first[i] != second[i]) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Test an object if this URI is equal to another.
	 *
	 * @param obj
	 *            an object to compare
	 * @return true if two URI objects are equal
	 */
	@Override
	public boolean equals(Object obj) {

		// normalize and test each components
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof URI)) {
			return false;
		}
		URI another = (URI) obj;
		// scheme
		if (!equals(aScheme, another.aScheme)) {
			return false;
		}
		// is_opaque_part or is_hier_part? and opaque
		if (!equals(aOpaque, another.aOpaque)) {
			return false;
		}
		// is_hier_part
		// has_authority
		if (!equals(aAuthority, another.aAuthority)) {
			return false;
		}
		// path
		if (!equals(aPath, another.aPath)) {
			return false;
		}
		// has_query
		if (!equals(aQuery, another.aQuery)) {
			return false;
		}
		// has_fragment? should be careful of the only fragment case.
		if (!equals(aFragment, another.aFragment)) {
			return false;
		}
		return true;
	}

	/**
	 * bla
	 *
	 * @param oos
	 *            bla
	 * @throws IOException
	 *             bla
	 */
	private void writeObject(ObjectOutputStream oos) throws IOException {
		oos.defaultWriteObject();
	}

	/**
	 * bla
	 *
	 * @param ois
	 *            bla
	 * @throws ClassNotFoundException
	 *             bla
	 * @throws IOException
	 *             bla
	 */
	private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
		ois.defaultReadObject();
	}

	/**
	 * Return a hash code for this URI.
	 *
	 * @return a has code value for this URI
	 */
	@Override
	public int hashCode() {
		if (hash == 0) {
			char[] ccc = aUri;
			if (ccc != null) {
				for (char element : ccc) {
					hash = 31 * hash + element;
				}
			}
			ccc = aFragment;
			if (ccc != null) {
				for (char element : ccc) {
					hash = 31 * hash + element;
				}
			}
		}
		return hash;
	}

	/**
	 * Compare this URI to another object.
	 *
	 * @param another
	 *            the object to be compared.
	 * @return 0, if it's same, -1, if failed, first being compared with in the
	 *         authority component
	 */
	@Override
	public int compareTo(URI another) {

		if (!equals(aAuthority, another.aAuthority)) {
			return -1;
		}
		return toString().compareTo(another.toString());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized Object clone() throws CloneNotSupportedException {

		URI instance = (URI) super.clone();

		instance.aUri = aUri;
		instance.aScheme = aScheme;
		instance.aOpaque = aOpaque;
		instance.aAuthority = aAuthority;
		instance.aUserinfo = aUserinfo;
		instance.aHost = aHost;
		instance.aPort = aPort;
		instance.aPath = aPath;
		instance.aQuery = aQuery;
		instance.aFragment = aFragment;
		// the charset to do escape encoding for this instance
		instance.protocolCharset = protocolCharset;
		// flags
		instance.ishierpart = ishierpart;
		instance.isopaquepart = isopaquepart;
		instance.isnetpath = isnetpath;
		instance.isabspath = isabspath;
		instance.isrelpath = isrelpath;
		instance.isregname = isregname;
		instance.isServer = isServer;
		instance.ishostname = ishostname;
		instance.isIPv4address = isIPv4address;
		instance.isIPv6reference = isIPv6reference;

		return instance;
	}

	// ------------------------------------------------------------ Get the URI

	/**
	 * It can be gotten the URI character sequence. It's raw-escaped. For the
	 *
	 * @return the URI character sequence
	 */
	public char[] getRawURI() {
		return aUri.clone();
	}

	/**
	 * It can be gotten the URI character sequence. It's escaped. For the
	 *
	 * @return the escaped URI string
	 */
	public String getEscapedURI() {
		return (aUri == null) ? null : new String(aUri);
	}

	/**
	 * It can be gotten the URI character sequence.
	 *
	 * @return the original URI string
	 * @throws com.adobe.cq.email.core.components.internal.request.URIException
	 *             incomplete trailing escape pattern or unsupported character
	 *             encoding
	 * @see #decode
	 */
	public String getURI() throws com.adobe.cq.email.core.components.internal.request.URIException {
		return (aUri == null) ? null : decode(aUri, getProtocolCharset());
	}

	/**
	 * Get the URI reference character sequence.
	 *
	 * @return the URI reference character sequence
	 */
	public char[] getRawURIReference() {
		if (aFragment == null) {
			return aUri.clone();
		}
		if (aUri == null) {
			return aFragment.clone();
		}
		// if _uri != null && _fragment != null
		String uriReference = new String(aUri) + "#" + new String(aFragment);
		return uriReference.toCharArray();
	}

	/**
	 * Get the escaped URI reference string.
	 *
	 * @return the escaped URI reference string
	 */
	public String getEscapedURIReference() {
		char[] uriReference = getRawURIReference();
		return new String(uriReference);
	}

	/**
	 * Get the original URI reference string.
	 *
	 * @return the original URI reference string
	 * @throws com.adobe.cq.email.core.components.internal.request.URIException
	 *             If {@link #decode} fails.
	 */
	public String getURIReference() throws com.adobe.cq.email.core.components.internal.request.URIException {
		char[] uriReference = getRawURIReference();
		return decode(uriReference, getProtocolCharset());
	}

	/**
	 * Get the escaped URI string.
	 *
	 * @return the escaped URI string
	 * @see #clone()
	 */
	@Override
	public String toString() {
		return getEscapedURI();
	}

	/**
	 * A mapping to determine the (somewhat arbitrarily) preferred charset for
	 */
	public static class LocaleToCharsetMap {

		private static final String ISO_8859_2 = "ISO-8859-2";
		private static final String ISO_8859_1 = "ISO-8859-1";
		private static final String ISO_8859_5 = "ISO-8859-5";
		private static final HashMap<String, String> LOCALE_TO_CHARSET_MAP;
		static {
			LOCALE_TO_CHARSET_MAP = new HashMap<>();
			LOCALE_TO_CHARSET_MAP.put("ar", "ISO-8859-6");
			LOCALE_TO_CHARSET_MAP.put("be", ISO_8859_5);
			LOCALE_TO_CHARSET_MAP.put("bg", ISO_8859_5);
			LOCALE_TO_CHARSET_MAP.put("ca", ISO_8859_1);
			LOCALE_TO_CHARSET_MAP.put("cs", ISO_8859_2);
			LOCALE_TO_CHARSET_MAP.put("da", ISO_8859_1);
			LOCALE_TO_CHARSET_MAP.put("de", ISO_8859_1);
			LOCALE_TO_CHARSET_MAP.put("el", "ISO-8859-7");
			LOCALE_TO_CHARSET_MAP.put("en", ISO_8859_1);
			LOCALE_TO_CHARSET_MAP.put("es", ISO_8859_1);
			LOCALE_TO_CHARSET_MAP.put("et", ISO_8859_1);
			LOCALE_TO_CHARSET_MAP.put("fi", ISO_8859_1);
			LOCALE_TO_CHARSET_MAP.put("fr", ISO_8859_1);
			LOCALE_TO_CHARSET_MAP.put("hr", ISO_8859_2);
			LOCALE_TO_CHARSET_MAP.put("hu", ISO_8859_2);
			LOCALE_TO_CHARSET_MAP.put("is", ISO_8859_1);
			LOCALE_TO_CHARSET_MAP.put("it", ISO_8859_1);
			LOCALE_TO_CHARSET_MAP.put("iw", "ISO-8859-8");
			LOCALE_TO_CHARSET_MAP.put("ja", "Shift_JIS");
			LOCALE_TO_CHARSET_MAP.put("ko", "EUC-KR");
			LOCALE_TO_CHARSET_MAP.put("lt", ISO_8859_2);
			LOCALE_TO_CHARSET_MAP.put("lv", ISO_8859_2);
			LOCALE_TO_CHARSET_MAP.put("mk", ISO_8859_5);
			LOCALE_TO_CHARSET_MAP.put("nl", ISO_8859_1);
			LOCALE_TO_CHARSET_MAP.put("no", ISO_8859_1);
			LOCALE_TO_CHARSET_MAP.put("pl", ISO_8859_2);
			LOCALE_TO_CHARSET_MAP.put("pt", ISO_8859_1);
			LOCALE_TO_CHARSET_MAP.put("ro", ISO_8859_2);
			LOCALE_TO_CHARSET_MAP.put("ru", ISO_8859_5);
			LOCALE_TO_CHARSET_MAP.put("sh", ISO_8859_5);
			LOCALE_TO_CHARSET_MAP.put("sk", ISO_8859_2);
			LOCALE_TO_CHARSET_MAP.put("sl", ISO_8859_2);
			LOCALE_TO_CHARSET_MAP.put("sq", ISO_8859_2);
			LOCALE_TO_CHARSET_MAP.put("sr", ISO_8859_5);
			LOCALE_TO_CHARSET_MAP.put("sv", ISO_8859_1);
			LOCALE_TO_CHARSET_MAP.put("tr", "ISO-8859-9");
			LOCALE_TO_CHARSET_MAP.put("uk", ISO_8859_5);
			LOCALE_TO_CHARSET_MAP.put("zh", "GB2312");
			LOCALE_TO_CHARSET_MAP.put("zh_TW", "Big5");
		}

		/**
		 * bla
		 *
		 * @param locale
		 *            bla
		 * @return bla
		 */
		private static String getCharset(Locale locale) {
			String charset = LOCALE_TO_CHARSET_MAP.get(locale.toString());
			if (charset != null) {
				return charset;
			}
			charset = LOCALE_TO_CHARSET_MAP.get(locale.getLanguage());
			return charset; // may be null
		}

	}

	/**
	 * bla
	 *
	 * @param data
	 *            bla
	 * @param charset
	 *            bla
	 * @return bla
	 */
    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings(
        value="DM_DEFAULT_ENCODING",
        justification="Fallback handling.")
	private static byte[] getBytes(final String data, String charset) {

		if (data == null) {
			throw new IllegalArgumentException("data may not be null");
		}

		if (charset == null || charset.length() == 0) {
			throw new IllegalArgumentException("charset may not be null or empty");
		}

		try {
			return data.getBytes(charset);
		} catch (UnsupportedEncodingException e) {
			return data.getBytes();
		}
	}

	/**
	 * bla
	 *
	 * @param data
	 *            bla
	 * @return bla
	 */
	private static String getAsciiString(final byte[] data) {

		if (data == null) {
			throw new IllegalArgumentException("Parameter may not be null");
		}

		try {
			return new String(data, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new com.adobe.cq.email.core.components.internal.request.URIException("HttpClient requires ASCII support");
		}
	}

	/**
	 * Converts the byte array of HTTP content characters to a string. If the
	 * specified charset is not supported, default system encoding is used.
	 *
	 * @param data
	 *            the byte array to be encoded
	 * @param charset
	 *            the desired character encoding
	 * @return The result of the conversion.
	 * @since 3.0
	 */
    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings(
        value="DM_DEFAULT_ENCODING",
        justification="Fallback handling.")
	public static String getString(final byte[] data, String charset) {

		if (data == null) {
			throw new IllegalArgumentException("Parameter may not be null");
		}

		if (charset == null || charset.length() == 0) {
			throw new IllegalArgumentException("charset may not be null or empty");
		}

		try {
			return new String(data, charset);
		} catch (UnsupportedEncodingException e) {

			return new String(data);
		}
	}

	/**
	 * Converts the specified string to byte array of ASCII characters.
	 *
	 * @param data
	 *            the string to be encoded
	 * @return The string as a byte array.
	 * @since 3.0
	 */
	public static byte[] getAsciiBytes(final String data) {

		if (data == null) {
			throw new IllegalArgumentException("Parameter may not be null");
		}

		try {
			return data.getBytes("US-ASCII");
		} catch (UnsupportedEncodingException e) {
			throw new com.adobe.cq.email.core.components.internal.request.URIException("HttpClient requires ASCII support");
		}
	}

	/**
	 * Bla.
	 *
	 * @param urlsafe
	 *            bla
	 * @param bytes
	 *            bla
	 * @return bla
	 */
	public static final byte[] encodeUrl(BitSet urlsafe, byte[] bytes) {
		if (bytes == null) {
			return null;
		}

		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		for (byte ccc : bytes) {
			int bbb = ccc;
			if (bbb < 0) {
				bbb = BYTES_256 + bbb;
			}
			if (urlsafe.get(bbb)) {
				if (bbb == ' ') {
					bbb = '+';
				}
				buffer.write(bbb);
			} else {
				buffer.write('%');
				char hex1 = Character.toUpperCase(Character.forDigit((bbb >> 4) & HEX_0X_F, BYTES_16));
				char hex2 = Character.toUpperCase(Character.forDigit(bbb & HEX_0X_F, BYTES_16));
				buffer.write(hex1);
				buffer.write(hex2);
			}
		}
		return buffer.toByteArray();
	}

	/**
	 * Bla.
	 *
	 * @param bytes
	 *            bla
	 * @return bla
	 */
	protected static final byte[] decodeUrl(byte[] bytes) {
		if (bytes == null) {
			return null;
		}
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		for (int i = 0; i < bytes.length; i++) {
			int bbb = bytes[i];
			if (bbb == '+') {
				buffer.write(' ');
			} else if (bbb == '%') {
				try {
					int uuu = Character.digit((char) bytes[++i], BYTES_16);
					int lll = Character.digit((char) bytes[++i], BYTES_16);
					if (uuu == -1 || lll == -1) {
						throw new com.adobe.cq.email.core.components.internal.request.URIException("Invalid URL encoding");
					}
					buffer.write((char) ((uuu << 4) + lll));
				} catch (ArrayIndexOutOfBoundsException e) {
					throw new com.adobe.cq.email.core.components.internal.request.URIException("Invalid URL encoding");
				}
			} else {
				buffer.write(bbb);
			}
		}
		return buffer.toByteArray();
	}

	boolean isIPv4address() {
		return isIPv4address;
	}
}
