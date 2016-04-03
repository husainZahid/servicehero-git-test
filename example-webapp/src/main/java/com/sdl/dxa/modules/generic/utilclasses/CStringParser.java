package com.sdl.dxa.modules.generic.utilclasses;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.CharArrayWriter;
import java.io.Reader;
import java.sql.Clob;
import java.util.StringTokenizer;

/**
 * Created by DELL on 11/15/2015.
 */
public class CStringParser {

	private static final String dblQuotCharacter = "\"";
    private static Logger logger= LoggerFactory.getLogger(CStringParser.class);
	/**
	 * This method replaces the special charactes with corresponding ascii code
	 * to store them into database accurately
	 *
	 * @param strValue contains string to be parsed
	 * @return String the parsed string with the corresponding ascii code
	 */
	public static String parseQuot(String strValue)
	{
		return parseQuot(strValue, false, true);
	}


	/**
	 * This method replaces the special charactes with corresponding ascii code
	 * to store them into database accurately
	 *
	 * @param strValue    contains string to be parsed
	 * @param bLineBreaks contains true if you want "\n" to be eliminated with <br>
	 * @return String the parsed string with the corresponding ascii code
	 */
	public static String parseQuot(String strValue, boolean bLineBreaks)
	{
		return parseQuot(strValue, bLineBreaks, true);
	}

	public static String parseQuot(String strValue, boolean bLineBreaks, boolean bKeepLineBreaks)
	{
		return parseQuot(strValue, bLineBreaks, bKeepLineBreaks, false);
	}

	/**
	 * This method replaces the special charactes with corresponding ascii code
	 * to store them into database accurately
	 *
	 * @param strValue        contains string to be parsed
	 * @param bLineBreaks     contains true if you want "\n" to be eliminated with <br>
	 * @param bKeepLineBreaks contains true if you want to retain the "\n" characters in the source string
	 * @param isDisplay       contains true if the method is being called from a page which is responsible for generating some site display HTML code
	 *                        If it is true, then the single quote characters read from the database are not replaced.
	 * @return String the parsed string with the corresponding ascii code
	 */
	public static String parseQuot(String strValue, boolean bLineBreaks, boolean bKeepLineBreaks, boolean isDisplay)
	{
		StringBuffer newString = new StringBuffer("");

		if (strValue.length() > 0 && strValue != null)
		{
			//strValue = parseMultiSingleQuot(strValue);
			char strChar;
			for (int i = 0; i < strValue.length(); i++)
			{
				strChar = strValue.charAt(i);
				//System.out.println("Before: " + strChar + "\t" + ((int)strChar));
				switch (strChar)
				{
					case 130:
                    case 131:
                    case 194:
                    case 195:
                        break;

                    case 10:    //\r
						if (i > 0)
						{
							if (bKeepLineBreaks)
							{
								newString.append(strChar);
								//strTempString = strTempString + strChar;
							}
							else if (strValue.charAt(i - 1) != 13 && bLineBreaks)
							{
								newString.append("<br>");
								//strTempString = strTempString + "<br>";
							}
						}
						break;

					case 13:    //\n
						if (i < strValue.length())
						{
							if (bKeepLineBreaks)
							{
								newString.append(strChar);
								//strTempString = strTempString + strChar;
							}
							else if (strValue.charAt(i + 1) == 10 && bLineBreaks)
							{
								newString.append("<br>");
								//strTempString = strTempString + "<br>";
							}
						}
						break;

					case 34:
					case 212:
					case 213:
					case 147:
					case 148:
						newString.append(dblQuotCharacter);
						//strTempString = strTempString + "&quot;";
						break;
					case 139:
						newString.append("<");
						break;
					case 149:
						newString.append("*");
						break;
					case 155:
						newString.append(">");
						break;

					case 39:
					case 210:
					case 211:
					case 145:
					case 146:
					case 180:
						if (!isDisplay)
						{
							newString.append("''");
							//strTempString = strTempString + "''";
						}
						else
						{
							newString.append(strChar);
							//strTempString = strTempString + strChar;
						}
						break;

					case 150:
					case 151:
					case 173:
					case 190:
						newString.append("-");
						//strTempString = strTempString + "-";
						break;

					default:
						newString.append(strChar);
						//strTempString = strTempString + strChar;
						break;

				}
				//System.out.println(strChar + ":" + (int)strChar + "\t" + strNewChar + ":" + (int)strNewChar.toCharArray()[0]);
	            //System.out.println(strChar + ":" + (int)strChar + "\t" + newString.toString());
			}
			//strValue = newString.toString();
			//strValue = strTempString;
		}
		return newString.toString();
	}

	/**
	 * This method replaces the special charactes with corresponding ascii code
	 * to store them into database accurately
	 *
	 * @param strValue  contains string to be parsed
	 * @param isDisplay contains true if the method is being called from a page which is responsible for generating some site display HTML code
	 *                  If it is true, then the single quote characters read from the database are not replaced.
	 * @return String the parsed string with the corresponding ascii code
	 */
	public static String parseQuotArabic(String strValue, boolean isDisplay)
	{
		StringBuffer newString = new StringBuffer("");

		if (strValue.length() > 0 && strValue != null)
		{
			//strValue = parseMultiSingleQuot(strValue);
			char strChar;
			for (int i = 0; i < strValue.length(); i++)
			{
				strChar = strValue.charAt(i);
				//System.out.println("Before: " + strChar + "\t" + ((int)strChar));
				switch (strChar)
				{
					case '\'':
						if (!isDisplay)
						{
							newString.append("''");
							//strTempString = strTempString + "''";
						}
						else
						{
							newString.append(strChar);
							//strTempString = strTempString + strChar;
						}
						break;

					default:
						newString.append(strChar);
						//strTempString = strTempString + strChar;
						break;

				}
				//System.out.println(strChar + ":" + (int)strChar + "\t" + strNewChar + ":" + (int)strNewChar.toCharArray()[0]);
			}
			//strValue = strTempString;
		}
		return newString.toString();
	}

	/**
	 * This method replaces multiple consecutive occurences of white spaces
	 * with a single white space
	 *
	 * @param strValue String from which white spaces have to be trimmed
	 * @return String the trimmed string with the single white spaces
	 */
	public static String trimSpaces(String strValue)
	{
		String strChar = "", strPrevChar = "";
		strValue = strValue.trim();
		StringBuffer newString = new StringBuffer("");

		//System.out.println("Before: " + strValue);
		for (int i = 0; i < strValue.length(); i++)
		{
			strChar = strValue.substring(i, i + 1);

			if (strChar.equals(" "))
			{
				if (!strChar.equals(strPrevChar))
					newString.append(strChar);
				//strTempString = strTempString + strChar;
			}
			else
				newString.append(strChar);
			//strTempString = strTempString + strChar;

			strPrevChar = strChar;
		}
		//System.out.println("After: " + strTempString);
		return newString.toString();
	}

	//Method to set the length of a given string to N number of characters
	//taking into account that no words are truncated
	//(that is the string is truncated only at a white space)
	/**
	 * This method sets the length of a given string to N number of characters
	 * taking into account that no words are truncated
	 * (that is the string is truncated only at a white space)
	 *
	 * @param strValue String whose length is to be set
	 * @param iLen     Number of characters to which the length is to be set
	 * @return String the trimmed string with the single white spaces
	 */
	public static String setLength(String strValue, int iLen)
	{
		String strSpecialChars = "!.%$";
		//StringBuffer newString = new StringBuffer("");

		if (iLen < strValue.length())
		{
			//System.out.println("Original (" + iLen + "): " + strValue);

			String sTemp = strValue.substring(0, iLen - 1);
			String sNextChar = strValue.substring(iLen - 1, iLen);

			//System.out.println("Before: " + sTemp);

			if (strSpecialChars.indexOf(sNextChar) >= 0)
				sTemp = sTemp + sNextChar;
			else
			{
				if (!sNextChar.equals(" "))
					sTemp = sTemp.substring(0, sTemp.lastIndexOf(" "));
			}

			//&& !sNextChar.equals(" "))

			//System.out.println("After: " + sTemp);
			return sTemp;
		}
		else
			return strValue;
	}


	/**
	 * This function parse string with strValue2 variable and fill <ul> and <li> tag to represent the content with bullets
	 *
	 * @param strValue1 contains String for parsing
	 * @param strValue2 contains String to parse
	 * @return String with tags for display in HTML form
	 */
	public static String parseIndent(String strValue1, String strValue2)
	{
		boolean isMore = false;
		StringBuffer newString = new StringBuffer("");
		int iLoc = 0;
		if (strValue1.toUpperCase().indexOf(strValue2.toUpperCase(), iLoc) > 0)
		{
			newString.append("<LI>");
		}

		for (int i = 0; i < strValue1.length(); i++)
		{
			iLoc = strValue1.toUpperCase().indexOf(strValue2.toUpperCase(), 0);
			if (iLoc >= 0)
			{
				isMore = true;
				newString.append(strValue1.substring(0, iLoc));
                newString.append("</LI><LI> ");
				//newString.append(strValue1.substring(iLoc + 1, strValue1.length()));
				strValue1 = strValue1.substring(iLoc + strValue2.length());
				//System.out.println(i + "    " + strValue1);
			}
			else
			{
				newString.append(strValue1);
				break;
			}

		}
		if (isMore)
		{
			newString.append("</LI></UL>");
			newString.insert(0, "<UL> ");
		}
		return newString.toString();
	}


	/**
	 * This function parse string with strValue2 variable and fill <ul> and <li> tag to represent the content with bullets and also puts two <br> statement where double "\n\n" present
	 * this function is used in equate
	 *
	 * @param strValue1 contains String for parsing
	 * @param strValue2 contains String to parse
	 * @return String with tags for display in HTML form
	 */
	public static String parseIndent1(String strValue1, String strValue2)
	{
		boolean isMore = false;
		StringBuffer newString = new StringBuffer("");
		int iLoc = 0;
		if (strValue1.toUpperCase().indexOf(strValue2.toUpperCase(), iLoc) > 0)
		{
			newString.append("<LI>");
		}

		for (int i = 0; i < strValue1.length(); i++)
		{
			iLoc = strValue1.toUpperCase().indexOf(strValue2.toUpperCase(), 0);
			//System.out.println(iLoc);
			if (iLoc >= 0)
			{
				isMore = true;
				newString.append(strValue1.substring(0, iLoc));
				//System.out.println("*" + strValue1.substring(iLoc+strValue2.length(), iLoc+strValue2.length()+strValue2.length()) + "*    *" + strValue2 + "*");
				if (iLoc != 1)
					newString.append(" </LI><LI> ");
				else
				{
					newString.delete(newString.length() - 11, newString.length());
					newString.append(" <br><br> ");
				}
/*
				//if(strValue1.substring(0 + (1 * strValue2.length()), 0 + (2 * strValue2.length())).equals(strValue2)) {
					System.out.println("in");
					newString.append(" <br> ");
					strValue1 = strValue1.substring(iLoc + strValue2.length());
					iLoc = iLoc + (1 * strValue2.length() + 1);
				}
*/
				//System.out.println(strValue2.length());
				//newString.append(strValue1.substring(iLoc + 1, strValue1.length()));
				strValue1 = strValue1.substring(iLoc + strValue2.length());
				//System.out.println(i + "    " + strValue1);
			}
			else
			{
				newString.append(strValue1);
				break;
			}

		}
		if (isMore)
		{
			newString.append("</LI></UL>");
			newString.insert(0, "<UL> ");
		}
		return newString.toString();
	}

	/**
	 * this method checks whether the string contains multiple single quot and replace that multiple single quot into one
	 * single quote
	 * @param strValue  contains string for parsing
	 * @return
	 */

/*
	public static String parseMultiSingleQuot(String strValue)
	{
        System.out.println("Before: " + strValue);
		while(strValue.indexOf("''") > -1)
		{
            strValue.replaceFirst("''", "'");
		}

		System.out.println("After: " + strValue);
    	return strValue;
	}
*/
//Dhananjay
	/**
	 * this method replaces a character in existing string with the new character/string
	 * which is passed to it
	 *
	 * @param s       contains source string
	 * @param oldChar the character to be replaced
	 * @param newChar contains the new character/String
	 * @return new string
	 */

	public static String replace(String s, char oldChar, String newChar)
	{
		char[] myCharArray = s.toCharArray();

		StringBuffer sb = new StringBuffer();
		for (int counter = 0; counter < myCharArray.length; counter++)
		{
			if (myCharArray[counter] == oldChar)
				sb.append(newChar);
			else
				sb.append(myCharArray[counter]);
		}

		return sb.toString();
	}

	public static String replace(String s, String oldString, String newString)
	{
		return replace(s, oldString, newString, false);
	}

    public static String replace(String s, String oldString, String newString, boolean bCaseSensitive)
    {
        StringBuffer stringbuffer = new StringBuffer("");
        int i;
        if(bCaseSensitive)
            i = s.indexOf(oldString);
        else
            i = s.toLowerCase().indexOf(oldString.toLowerCase());
        if (i < 0)
            stringbuffer.append(s);
        else
        {
            if (i > 0)
                stringbuffer.append(s.substring(0, i));
            stringbuffer.append(newString);
            if (i + oldString.length() < s.length())
                stringbuffer.append(replace(s.substring(i + oldString.length()), oldString, newString, bCaseSensitive));
        }
        return stringbuffer.toString();
    }


    /**
	 * this method replaces a space with %20 need to
	 * embed the string in URL or Href tag
	 *
	 * @param s contains source string
	 * @return new string
	 */

	public static String strEmb(String s)
	{
		char[] myCharArray = s.toCharArray();
		StringBuffer sb = new StringBuffer();
		for (int counter = 0; counter < myCharArray.length; counter++)
		{
			switch (myCharArray[counter])
			{
				case 32:
					sb.append("%20");
					break;
				default :
					sb.append(myCharArray[counter]);
			}
		}

		return sb.toString();
	}

	/**
	 * this method replaces a occurence of \n with <br>
	 * which is passed to it
	 *
	 * @param s contains source string
	 * @return new string
	 */

	public static String showBR(String s)
	{
		char[] myCharArray = s.toCharArray();
		StringBuffer sb = new StringBuffer();
		for (int counter = 0; counter < myCharArray.length; counter++)
		{
			switch (myCharArray[counter])
			{
				case 13:
					sb.append("<br>");
					break;
				default :
					sb.append(myCharArray[counter]);
			}
		}

		return sb.toString();
	}

	/**
	 * this method excepts an String array and converts it into string
	 * the values are seperated by comma.
	 *
	 * @param s contains array of source string
	 * @return new string
	 */

	public static String strAryToStr(String[] s)
	{
		StringBuffer sb = new StringBuffer();
		for (int counter = 0; counter < s.length; counter++)
		{
			if ((counter + 1) != s.length)
				sb.append(s[counter] + ",");
			else
				sb.append(s[counter]);
		}
		return sb.toString();
	}

	/**
	 * this method excepts comma separated String converts it into array
	 *
	 * @param s contains comma separated source string
	 * @return new array
	 */

	public static String[] strStrToAry(String s)
	{
		StringTokenizer objS = new StringTokenizer(s, ",");
		int iToken = objS.countTokens();
		String[] strRet = new String[iToken];
		for (int counter = 0; counter < iToken; counter++)
			strRet[counter] = objS.nextToken();
		return strRet;
	}

	/**
	 * this method excepts an String array and converts it into string
	 * the values are seperated by comma.
	 *
	 * @param s contains array of source string
	 * @return new string
	 */

	/*public static String strEDAryToStr(String[] s)
	{
		StringBuffer sb = new StringBuffer();
		AESEncryption ae = new AESEncryption();
		for (int counter = 0; counter < s.length; counter++)
		{
            if ((counter + 1) != s.length)
				sb.append(ae.decryptParam(s[counter]) + ",");
			else
				sb.append(ae.decryptParam(s[counter]));
		}
		return sb.toString();
	}
       */

    public static String strEDAryToStr(String[] s, AESEncryption encrypterDecrypter)
    {
        StringBuffer sb = new StringBuffer();
        for (int counter = 0; counter < s.length; counter++)
        {
            if ((counter + 1) != s.length)
                sb.append(encrypterDecrypter.decrypt(s[counter]) + ",");
            else
                sb.append(encrypterDecrypter.decrypt(s[counter]));
        }
        return sb.toString();
    }


	/**
	 * this method parse a string and eliminat all the charactors axcept numeric charactors
	 *
	 * @param strValue contains string to be parsed
	 * @param bpercent contains true % sign is to be removed and false if you want % sign in string
	 * @return a parsed string
	 */
	public static String parseNumeric(String strValue, boolean bpercent)
	{
		String strCharNotToRemove = "";
		if (bpercent)
			strCharNotToRemove = "0123456789.+-";
		else
			strCharNotToRemove = "0123456789.+-%";
		char[] myCharArray = strValue.toCharArray();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < myCharArray.length; i++)
		{
			if (strCharNotToRemove.indexOf(myCharArray[i]) != -1)
				sb.append(myCharArray[i]);
		}
		return sb.toString();
	}

	public static String parseNumeric(String strValue)
	{
		return parseNumeric(strValue, false);
	}

    public static String getStringFromClob(Clob clbValue) {
        String text = null;
        try {
            Reader read = clbValue.getCharacterStream();
            if (read != null) {
                CharArrayWriter writer = new CharArrayWriter();
                char[] buf = new char[1024];
                int count = read.read(buf, 0, buf.length);
                while (count > 0) {
                    writer.write(buf, 0, count );
                    count = read.read(buf, 0, buf.length);
                }
                read.close();
                text = new String(writer.toCharArray());
                writer.close();
            }
        } catch(Exception e) {
            // below changes done for using Logback by Jayanthi
            logger.error(e.getMessage(),e);

            //System.out.println(GetErrorInfo.getErrorInfo(e, "CStringParser"));
           // e.printStackTrace();
        }
        return text;
    }

    public static String getStringToClob(Clob clbValue) {
        String text = null;
        try {
            Reader read = clbValue.getCharacterStream();
            if (read != null) {
                CharArrayWriter writer = new CharArrayWriter();
                char[] buf = new char[1024];
                int count = read.read(buf, 0, buf.length);
                while (count > 0) {
                    writer.write(buf, 0, count );
                    count = read.read(buf, 0, buf.length);
                }
                read.close();
                text = new String(writer.toCharArray());
                writer.close();
            }
        } catch(Exception e) {
            // below changes done for using Logback by Jayanthi
            logger.error(e.getMessage(),e);

            //System.out.println(GetErrorInfo.getErrorInfo(e, "CStringParser"));
            //e.printStackTrace();
        }
        return text;
    }


    public static String parseXMLText(String s, String tags)
    {

        String tmpValue = s;
        String finalValue = "", strTemp = "";
        String strfinal = tmpValue.substring(tmpValue.lastIndexOf("</" + tags + ">")+tags.length() + 3);
        try{
            while(tmpValue.indexOf("<" + tags + ">") > -1)
            {
                finalValue = finalValue + tmpValue.substring(0, tmpValue.indexOf("<" + tags + ">"));
                strTemp = tmpValue.substring(tmpValue.indexOf("<" + tags + ">") + tags.length() + 2, tmpValue.indexOf("</" + tags + ">"));
                strTemp = CStringParser.replace(strTemp, "&lt;", "<");
                strTemp = CStringParser.replace(strTemp, "&gt;", ">");
                strTemp = CStringParser.replace(strTemp, "&quot;", "\"");
                strTemp = CStringParser.replace(strTemp, "&apos;", "'");
                strTemp = CStringParser.replace(strTemp, "&", "&amp;");
                strTemp = CStringParser.replace(strTemp, "<", "&lt;");
                strTemp = CStringParser.replace(strTemp, ">", "&gt;");
                strTemp = CStringParser.replace(strTemp, "\"", "&quot;");
                strTemp = CStringParser.replace(strTemp, "'", "&apos;");
                logger.error(strTemp);
                finalValue = finalValue + "<" + tags + ">" + strTemp + "</" + tags + ">";
                tmpValue = tmpValue.substring(tmpValue.indexOf("</" + tags + ">") + tags.length() + 3);
            }
            finalValue = finalValue + strfinal;
        }
        catch(Exception e) {
            System.out.println(e.getMessage() + e);
            e.printStackTrace();
        }
        return finalValue;
    }

    public static String doUpperCaps(String srcString, String strWordSeperator)
    {
        StringTokenizer words = new StringTokenizer(srcString, strWordSeperator);
        String sWord,  returnString = "";
        char firstCharacter;
        while(words.hasMoreTokens())
        {
            sWord = words.nextToken();
            firstCharacter = sWord.charAt(0);
            if(firstCharacter > 96 && firstCharacter < 122)
                firstCharacter -= 32;
            returnString += firstCharacter + sWord.substring(1);
            if(words.hasMoreTokens())
                returnString += " ";
        }
        return returnString;
    }


    // Email Obfuscator Code

    private static String v2;
	/**
	 * this method parse a string and checks if the string is arabic
	 *
	 * @param strValue contains string to be parsed
	 * @return return true or false
	 */
	public static boolean isProbablyArabic(String strValue) {
	    for (int i = 0; i < strValue.length();) {
	        int c = strValue.codePointAt(i);
	        if (c >= 0x0600 && c <=0x06E0)
	            return true;
	        i += Character.charCount(c);
	    }
	    return false;
	}

    public static String emailObfuscator(String strText)
    {
        StringBuffer objSb;
        String strEmail, strSubject, strClass, strTemp;
        String strSrch = "mailto:";
        int iStartPos, iEndPos;
        while((iStartPos = strText.indexOf(strSrch)) > -1)
        {
            objSb = new StringBuffer();
            iEndPos = strText.indexOf("</a>", iStartPos);
            strTemp = strText.substring(0, iStartPos);
            objSb.append((strTemp).substring(0, strTemp.lastIndexOf("<a")));
            strTemp = strText.substring(iStartPos + strSrch.length(), iEndPos);
            strEmail = strTemp.substring(0, strTemp.indexOf("\""));
            if(strEmail.indexOf("?") > -1)
                strSubject = "";
            else
                strSubject = "";
            strClass = strTemp.substring(strTemp.indexOf("class=") + 7);
            strClass = strClass.substring(0, strClass.indexOf("\""));
            //System.out.println(strEmail + "*   *" + strSubject +  "*    *" + strClass );
            objSb.append(updateView(strEmail, strSubject, strClass));
            objSb.append(strText.substring(iEndPos + 4));
            strText = objSb.toString();
        }
        return strText;
    }

    private static char[] baseC= new char[]
    {
      'A','8','C','D','B','F',
      'N','S','3','R','P','T',
      'G','K','Z','J','2','M',
      'U','Y','W','X','V','6',
      'Q','H','5','I','4','E'
    };

    private static String enc(String strEmail) {
        int rndNo;
        String encString ="";
        v2 = "";
        for(int i=0;i<strEmail.length();i++) {
            rndNo= (int) Math.floor(Math.random()*30);
            v2+=baseC[rndNo];
            encString += String.valueOf((char)(baseC[rndNo]^strEmail.charAt(i)));
            //System.out.println("baseC[rndNo]: " + baseC[rndNo] + "\tstrEmail.charAt(i): " + strEmail.charAt(i) + "\t" + (String.valueOf((char)(baseC[rndNo]^strEmail.charAt(i)))));
        }
        return encString;
    }

    private static boolean chk(String encStr, String strEmail) {
        String strTemp ="";
        for(int i=0;i<v2.length();i++)
        {
            if((char)(v2.charAt(i)^encStr.charAt(i)) == '\n' || (char)(v2.charAt(i)^encStr.charAt(i)) == '\r')
                return false;
            else
                strTemp += String.valueOf((char)(v2.charAt(i)^encStr.charAt(i)));
        }
        return (strTemp.equals(strEmail));
    }


    public static String updateView(String strEmail, String strSubject, String strClass)
    {
        String encStr = "";
        boolean isDone=false;
        while(!isDone) {
            encStr = enc(strEmail);
            if(encStr.indexOf("\\") == -1 )
                isDone=chk(encStr, strEmail);
        }
        encStr = CStringParser.replace(encStr, "'", "\\'");
        encStr = CStringParser.replace(encStr, "\"", "\\\"");
        StringBuffer objSb = new StringBuffer();
        objSb.append("<script language=\"javascript\" type=\"text/javascript\">");
	    objSb.append("var v2='" + v2 + "';");
	    //objSb.append("var v7=unescape('" + encStr + "');");
        objSb.append("var v7='" + encStr + "';");
	    objSb.append("var v1='';");
	    objSb.append("for(var i=0;i<v2.length;i++)");
	    objSb.append("v1+=String.fromCharCode(v2.charCodeAt(i)^v7.charCodeAt(i));");
	    //objSb.append("alert(v1);");
        if(strSubject.equals(""))
            objSb.append("document.write(' <a href=\"javascript:void(0)\" onclick=\"window.location=\\'mail\\u0074o\\u003a'+v1+'\\'\" class=\"" + strClass + "\">'+v1+'<\\/a>');");
        else
            objSb.append("document.write(' <a href=\"javascript:void(0)\" onclick=\"window.location=\\'mail\\u0074o\\u003a'+v1+'?subject=" + strSubject + "\\'\" class=\"" + strClass + "\">'+v1+'<\\/a>');");
	    objSb.append("</script>");
	    return objSb.toString();
    }

    public static String toTitleCase(String string)
    {
        String result = "";
        for (int i = 0; i < string.length(); i++)
        {
            String next = string.substring(i, i + 1);
            if (i == 0)
            {
                result += next.toUpperCase();
            }
            else
            {
                result += next.toLowerCase();
            }
        }

        /*
        BreakIterator wordBreaker = BreakIterator.getWordInstance();
        String str = "life is good";
        String words[] = new String[wordBreaker.last()];
        wordBreaker.first();
        wordBreaker.setText(str);
        int end = 0;

        String word;
        int iCount = 0;
        for(int start = wordBreaker.first(); (end= wordBreaker.next()) != BreakIterator.DONE; start=end)
            words[iCount++] = str.substring(start, end);

        for(iCount = 0; iCount < words.length; iCount++)
        {
        }
        */
        return result;
        }
}
