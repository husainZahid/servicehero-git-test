package com.sdl.dxa.modules.generic.utilclasses;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;
import java.util.TimeZone;

public class DateFunctions {
    private static Logger logger= LoggerFactory.getLogger(DateFunctions.class);

	public static int monthMaxDate[] = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    public static String day[] = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
    public static String monthShort[] = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    public static String month[] = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};

    public static String monthShortArabic[] = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    public static String monthArabic[] = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};

    
    /**
	 * This function is used to convert a date into given format with the help of SimpleDateformat class.
	 *
	 * @param  lValue contains date in format 01/01/2003
	 * @return a String contains date in format given format default is yyyymmdd (Khayal standard format)
	 */
	public static String getSimpleDate(long lValue)
    {
        return getSimpleDate(lValue, "yyyyMMdd");
    }

	public static String getSimpleDate(long lValue, String pattern)
    {
        if(lValue == 0)
            return "---";
        else
        {
        	Calendar objCal = new GregorianCalendar(TimeZone.getTimeZone("EAT"));
            objCal.setTimeInMillis(lValue);
        	return getSimpleDate(objCal, pattern);
        }	
    }

	public static String getSimpleDate(Calendar objCal) 
	{
    	return getSimpleDate(objCal, "yyyyMMdd");
    }

    public static String getSimpleDate(Calendar objCal, String pattern)
	{
    	SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
    	return dateFormat.format(objCal.getTime());
	}

	public static String getSimpleDate(Date objDate)
    {
    	return getSimpleDate(objDate, "yyyyMMdd");
    }

    public static String getSimpleDate(Date objDate, String pattern)
    {
    	SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
    	return dateFormat.format(objDate);
    }

	/**
	 * This function is used to convert a String object into Khayal's standard format of date YYYYMMDD.
	 * This YYYYMMDD format is used for sorting database through date field
	 *
	 * @param strValue contains date in format 01/01/2003
	 * @return a String contains date in format 20030101
	 */
	public static String getSimpleDate(String strValue)
	{
		return getSimpleDate(strValue, "/");
	}

	/**
	 * @param strValue1 contains date in format 01/01/2003
	 * @param strValue2 contains filler
	 * @return a String contains date in format 20030101
	 */
	public static String getSimpleDate(String strValue1, String strValue2)
	{
		return getSimpleDate(strValue1, strValue2, "ddmmyyyy");
	}

	public static String getSimpleDate(String strValue1, String strValue2, String strDateFormat)
	{
		return getSimpleDate(strValue1, strValue2, strDateFormat, "yyyyMMdd");
	}

	/**
	 * @param strValue1     contains date in format 01/01/2003
	 * @param strValue2     contains filler
	 * @param strDateFormat contains format of strValue1 (mmddyyyy or ddmmyyyy)
	 * @return a String contains date in format 20030101
	 */
	public static String getSimpleDate(String strValue1, String strValue2, String strDateFormat, String pattern)
	{
		String strDate = "", strMonth = "";
		int iYear = 0;
		StringTokenizer st = new StringTokenizer(strValue1, strValue2);

        if (strDateFormat.startsWith("yy"))  // ignore when format is "mmddyyyy" and "ddmmyyyy"
            iYear = Integer.parseInt(st.nextToken());

		if (!strDateFormat.startsWith("mm") && !strDateFormat.startsWith("yy"))  // ignore when format is "mmddyyyy" and "yyyymmdd"
		{
			strDate = st.nextToken();
			if (Integer.parseInt(strDate) < 10)
				strDate = "0" + Integer.parseInt(strDate);
		}

        if(strDateFormat.startsWith("mmmm"))
        {
            String mm = st.nextToken();
            for(int i = 0; i < month.length; i++)
            {
                if(month[i].equalsIgnoreCase(mm))
                    strMonth = "" + (i + 1);
            }
        }
        else if(strDateFormat.startsWith("mmm"))
        {
            String mm = st.nextToken();
            for(int i = 0; i < monthShort.length; i++)
            {
                if(monthShort[i].equalsIgnoreCase(mm))
                    strMonth = "" + (i + 1);
            }
        }
        else
            strMonth = st.nextToken();
		if (Integer.parseInt(strMonth) < 10)
			strMonth = "0" + Integer.parseInt(strMonth);

		if (strDateFormat.startsWith("mm") || strDateFormat.startsWith("yy"))   // ignore when format is "ddmmyyyy"
		{
			strDate = st.nextToken();
			if (Integer.parseInt(strDate) < 10)
				strDate = "0" + Integer.parseInt(strDate);
		}

		if (!strDateFormat.startsWith("yy"))   // ignore when format is "yyyymmdd"
            iYear = Integer.parseInt(st.nextToken());

        if (iYear < 100)
		{
			if (iYear < 50)
				iYear = 2000 + iYear;
			else
				iYear = 1900 + iYear;
		}
    	Calendar objCal = new GregorianCalendar(TimeZone.getTimeZone("EAT"));
        objCal.set(Calendar.YEAR, iYear);
        objCal.set(Calendar.MONTH, Integer.parseInt(strMonth) - 1);
        objCal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(strDate));
    	return getSimpleDate(objCal, pattern);
	}

    public static String getDisplayDateTime(Calendar objCal)
    {
        String strTemp = getDisplayDate(objCal, true) + " ";
        if (objCal.get(Calendar.HOUR_OF_DAY) < 10)
            strTemp += "0";
        strTemp += objCal.get(Calendar.HOUR_OF_DAY) + ":";
        if (objCal.get(Calendar.MINUTE) < 10)
            strTemp += "0";
        strTemp += objCal.get(Calendar.MINUTE) + ":";
        if (objCal.get(Calendar.SECOND) < 10)
            strTemp += "0";
        strTemp += objCal.get(Calendar.SECOND);
        //System.out.println(strTemp);
        return strTemp;
    }


	/**
	 * These set of functions are used to generate the date bye adding or substracting the days, months and years.
	 */
	public static String makeDate(long lValue, int iValue)
    {
        return makeDate(lValue, iValue, "dd");
    }

    public static String makeDate(long lValue, int iValue, String setValue)
    {
        Calendar objCal = new GregorianCalendar(TimeZone.getTimeZone("EAT"));
        objCal.setTimeInMillis(lValue);
        return makeDate(objCal, iValue, setValue);
    }

    public static String makeDate(Calendar objCal, int iValue)
    {
        return makeDate(objCal, iValue, "dd");
    }

    public static String makeDate(Calendar objCal, int iValue, String setValue)
    {
        if(setValue.equals("dd"))
            objCal.add(Calendar.DATE, iValue);
        if(setValue.equals("mm"))
            objCal.add(Calendar.MONTH, iValue);
        if(setValue.equals("yy"))
            objCal.add(Calendar.YEAR, iValue);
        return DateFunctions.getSimpleDate(objCal);
    }


	public static String makeDate(String sValue, int iValue)
	{
        return makeDate(sValue, iValue, "dd");
	}

    public static String makeDate(String sValue, int iValue, String setValue)
    {
        Calendar objCal = getDateFromString(sValue);
        if(setValue.equals("dd"))
            objCal.add(Calendar.DATE, iValue);
        if(setValue.equals("mm"))
            objCal.add(Calendar.MONTH, iValue);
        if(setValue.equals("yy"))
            objCal.add(Calendar.YEAR, iValue);
        return DateFunctions.getSimpleDate(objCal);
    }

	/**
	 * This function is used to get the calendar object from the khayal standard date format (yyyymmdd).
	 */
    public static Calendar getDateFromString(String sValue)
    {
        int iYear;
        Calendar objCal = new GregorianCalendar(TimeZone.getTimeZone("EAT"));
        objCal.set(Calendar.DATE, (Integer.parseInt(sValue.substring(6))));
        objCal.set(Calendar.MONTH, (Integer.parseInt(sValue.substring(4, 6)) - 1));
        iYear = Integer.parseInt(sValue.substring(0, 4));
        if(iYear < 1000) {
            if(iYear < 50)
                iYear = 1900 + iYear;
            else
                iYear = 2000 + iYear;
        }
        objCal.set(Calendar.YEAR, iYear);
        return objCal;
    }
    
    public static long compareDate(long tempDate, long tempDate1)
    {
        return ((tempDate1 - tempDate) / 86400000);
    }

	public static long compareDate(String tempDate, String tempDate1)
	{
		Calendar objCal1 = getDateFromStringFixTime(tempDate);
		Calendar objCal2 = getDateFromStringFixTime(tempDate1);
		return ((objCal2.getTime().getTime() - objCal1.getTime().getTime()) / 86400000);
	}

    public static Calendar getDateFromStringFixTime(String sValue)
    {
        Calendar objCal = new GregorianCalendar(TimeZone.getTimeZone("EAT"));
        objCal.set(Calendar.DATE, (Integer.parseInt(sValue.substring(6))));
        objCal.set(Calendar.MONTH, (Integer.parseInt(sValue.substring(4, 6)) - 1));
        objCal.set(Calendar.YEAR, Integer.parseInt(sValue.substring(0, 4)));
        objCal.set(Calendar.HOUR_OF_DAY, 0);
        objCal.set(Calendar.MINUTE, 0);
        objCal.set(Calendar.SECOND, 0);
        objCal.set(Calendar.MILLISECOND, 0);
        return objCal;
    }

    public static boolean isNewItem(long creationDate, String isNewItemFromField, int newPeriod) {
        boolean rtnValue = false;
        long newPreiodTime;
        long lCreationDate;
        newPreiodTime = 1000 * 60 * 60 * 24 * (long)newPeriod;
        //System.out.println(creationDate + "   " + isNewItemFromField +  "  " + newPeriod);
        lCreationDate = (getDateFromStringFixTime(getSimpleDate(creationDate))).getTimeInMillis();
        if(isNewItemFromField.equals("No")) {
            //System.out.println(lCreationDate +  "   " + newPreiodTime +  "  " + (lCreationDate + newPreiodTime) + "   " + System.currentTimeMillis());
            rtnValue = (lCreationDate + newPreiodTime > System.currentTimeMillis());
        } else
            rtnValue = true;
        return  rtnValue;
    }

    /**
    	 * This function is used to convert database date  into display format.
    	 *
    	 * @param strValue1 contains date from database in format YYYYMMDD
    	 * @return String contains date to be displayed
    	 */
    	public static String getDisplayDate(String strValue1)
    	{
    		return getDisplayDate(strValue1, "/");
    	}

    	/**
    	 * This function is used to convert database date  into display format.
    	 *
    	 * @param strValue1 contains date from database in format YYYYMMDD
    	 * @param strValue2 contains character for using as filler
    	 * @return contains date from database in format YYYYMMDD
    	 */
    	public static String getDisplayDate(String strValue1, String strValue2)
    	{
    		return getDisplayDate(strValue1, strValue2, "ddmmyyyy");
    	}

    	/**
    	 * This function is used to convert database date  into display format.
    	 *
    	 * @param strValue1     contains date from database in format YYYYMMDD
    	 * @param strValue2     contains character for using as filler
    	 * @param strDateFormat contains format in which date is displayed
    	 * @return contains date from database in format YYYYMMDD
    	 */
    	public static String getDisplayDate(String strValue1, String strValue2, String strDateFormat)
    	{
            return getDisplayDate(strValue1, strValue2, strDateFormat, 0);
    	}


        public static String getDisplayDate(String strValue1, String strValue2, String strDateFormat, int iLanguage)
        {
            return getDisplayDate(strValue1, strValue2, strDateFormat, iLanguage, readMonthNamesFromFile());
        }

        public static String getDisplayDate(String strValue1, String strValue2, String strDateFormat, int iLanguage, String monthNamesTextFilePath)
        {
            String strTemp;
            if (strValue1 != null && strValue1.length() > 0 && !strValue1.equals("0"))
            {
                strTemp = "";
                if (strDateFormat.equals("ddmm"))
                {
                    strTemp = strValue1.substring(6) + strValue2;
                    strTemp += strValue1.substring(4, 6);
                }
                else if (strDateFormat.equals("ddmmyy"))
                {
                    strTemp = strValue1.substring(6) + strValue2;
                    strTemp += strValue1.substring(4, 6) + strValue2;
                    strTemp += strValue1.substring(2, 4);
                }
                else if (strDateFormat.equals("ddmmyyyy"))
                {
                    strTemp = strValue1.substring(6) + strValue2;
                    strTemp += strValue1.substring(4, 6) + strValue2;
                    strTemp += strValue1.substring(0, 4);
                }
                else if (strDateFormat.equals("mmddyyyy"))
                {
                    strTemp = strValue1.substring(4, 6) + strValue2;
                    strTemp += strValue1.substring(6) + strValue2;
                    strTemp += strValue1.substring(0, 4);
                }
                else if (strDateFormat.equals("ddMMyyyy"))
                {
                    strTemp = strValue1.substring(6) + "-";
                    if(iLanguage == 0)
                        strTemp += month[Integer.parseInt(strValue1.substring(4, 6)) - 1].substring(0, 3) + "-";
                    else
                        strTemp += monthArabic[Integer.parseInt(strValue1.substring(4, 6)) - 1] + "-";
                    strTemp += strValue1.substring(2, 4);
                }
                else if (strDateFormat.equals("MMddyyyy"))
                {
                    if(iLanguage == 0)
                        strTemp = month[Integer.parseInt(strValue1.substring(4, 6)) - 1].substring(0, 3) + " ";
                    else
                        strTemp = monthArabic[Integer.parseInt(strValue1.substring(4, 6)) - 1] + " ";
                    strTemp += strValue1.substring(6) + strValue2 + " ";
                    strTemp += strValue1.substring(0, 4);
                }
                else if (strDateFormat.equals("YYYY-MM-DD"))
                {
                    strTemp = strValue1.substring(8) + strValue2;
                    strTemp += strValue1.substring(5, 7) + strValue2;
                    strTemp += strValue1.substring(0, 4);
                }
                else if (strDateFormat.equals("MM-dd-yyyy"))
                {
                    if(iLanguage == 0)
                        strTemp = month[Integer.parseInt(strValue1.substring(4, 6)) - 1] + " ";
                    else
                        strTemp = monthArabic[Integer.parseInt(strValue1.substring(4, 6)) - 1] + " ";
                    strTemp += strValue1.substring(6) + strValue2 + " ";
                    strTemp += strValue1.substring(0, 4);
                }
                else if (strDateFormat.equals("dd-MM-yyyy"))
                {
                    strTemp = strValue1.substring(6) + " ";
                    if(iLanguage == 0)
                        strTemp += month[Integer.parseInt(strValue1.substring(4, 6)) - 1] + strValue2 + " ";
                    else
                        strTemp += monthArabic[Integer.parseInt(strValue1.substring(4, 6)) - 1] + strValue2 + " ";
                    strTemp += strValue1.substring(0, 4);
                }
                else if (strDateFormat.equals("dd-mon-yyyy"))
                {
                    strTemp = strValue1.substring(6) + strValue2;
                    strTemp += monthShort[(Integer.parseInt(strValue1.substring(4, 6)) - 1)] + strValue2;
                    strTemp += strValue1.substring(0, 4);
                }
                else if (strDateFormat.equals("dd/mon/yyyy"))
                {
                    //String month[] = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
                    strTemp = strValue1.substring(0, 2) + strValue2;
                    strTemp += monthShort[(Integer.parseInt(strValue1.substring(3, 5)) - 1)].toUpperCase() + strValue2;
                    strTemp += strValue1.substring(6);
                }
            }
            else
                strTemp = "  --  ";
            return strTemp;

        }

    public static String getDisplayDate(Calendar objCal, boolean bFlag)
    	{
    		String strDate;
    		if (objCal.get(Calendar.DAY_OF_MONTH) < 10)
    			strDate = "0" + objCal.get(Calendar.DAY_OF_MONTH) + "/";
    		else
    			strDate = objCal.get(Calendar.DAY_OF_MONTH) + "/";
    		if ((objCal.get(Calendar.MONTH) + 1) < 10)
    			strDate += "0" + (objCal.get(Calendar.MONTH) + 1);
    		else
    			strDate += "" + (objCal.get(Calendar.MONTH) + 1);
    		if (bFlag)
    			strDate += "/" + objCal.get(Calendar.YEAR);
    		return strDate;
    	}

    public static String readMonthNamesFromFile()
        {
            String strPath;

            if(WhoAmI.getIPAddress().startsWith("192.168"))
            {
                if(WhoAmI.getIP().startsWith("khayal1"))
                    strPath = "c:\\websites\\utilclasses\\utilclasses\\";
                else
                    strPath = "d:\\websites\\utilclasses\\utilclasses\\";
            }
            else if (WhoAmI.getIP().startsWith("kc") && WhoAmI.getIP().endsWith("server") && (WhoAmI.getIP().indexOf("web")>0 || WhoAmI.getIP().indexOf("db")>0))
                strPath = "d:\\websites\\classes\\utilclasses\\";
            else if(WhoAmI.getIPAddress().startsWith("10.0.0.5") || WhoAmI.getIPAddress().startsWith("10.0.0.31"))
                strPath = "";
            else
                strPath = "d:\\websites\\classes\\utilclasses\\";

            int iCount = 0, iTokenNo = 12;
            String strData = "";
            StringTokenizer objToken = null;
            String strEMonth[] = new String[iTokenNo];
            String strAMonth[] = new String[iTokenNo];
            BufferedReader objIn = null;

            try
            {
                File monthNamesFile = new File(strPath + "MonthName.txt");
                if(monthNamesFile.exists())
                {
                    objIn = new BufferedReader(new InputStreamReader(new FileInputStream(monthNamesFile)));

                    objIn.readLine();
                    while ((strData = objIn.readLine()) != null)
                    {
                        strData = strData.trim();
                        if (strData.length() > 0)
                        {
                            objToken = new StringTokenizer(strData, "\t");
                            strEMonth[iCount] = objToken.nextToken();
                            strAMonth[iCount] = objToken.nextToken();
                            iCount++;
                        }
                        if (iCount > iTokenNo)
                            break;
                    }
                    objIn.close();

                    System.arraycopy(strEMonth, 0, month, 0, strEMonth.length);
                    System.arraycopy(strAMonth, 0, monthArabic, 0, strAMonth.length);
                }
            }
            catch (Exception e)
            {
    //            System.arraycopy(month, 0, strEMonth, 0, month.length);
    //            System.arraycopy(month, 0, strAMonth, 0, month.length);
                // below changes done for using Logback by Jayanthi
                logger.error(e.getMessage(),e);

                // System.out.println(GetErrorInfo.getErrorInfo(e, "CDateFunction"));
               // e.printStackTrace();
            }
            return strPath;
        }

}
