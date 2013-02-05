/**
 * 
 */
package ca.ubc.magic.utils;

import java.util.Locale;

import org.springframework.context.MessageSource;

/**
 * Wrapper for message source for localization
 * 
 * @author mike
 *
 */
public class Messages {
	private Locale locale = Locale.US;
	
	private MessageSource msg;

	public Messages(MessageSource msg) {
		this.msg = msg;
	}
	
	public String getMessage(String msgTag) {
		return msg.getMessage(msgTag, null, locale);
	}
}
