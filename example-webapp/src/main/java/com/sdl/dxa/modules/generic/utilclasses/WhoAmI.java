package com.sdl.dxa.modules.generic.utilclasses;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class WhoAmI
{

	private static String serverIP = "khayal1.khayal.net";

	/**
	 * Tells you the domain name and IP of the machine
	 * you are running.
	 */
	public static String getIP()
	{
		try
		{
			InetAddress localaddr = InetAddress.getLocalHost();
			serverIP = localaddr.getHostName();
		}
		catch (UnknownHostException e)
		{
            //System.out.println(GetErrorInfo.getErrorInfo(e, "WhoAmI"));
			System.err.println("Can't detect localhost : " + e);
		}

		return serverIP.toLowerCase();

	}

    /**
     * Tells you the domain name and IP of the machine
     * you are running.
     */
    public static String getIPAddress()
    {
        try
        {
            InetAddress localaddr = InetAddress.getLocalHost();
            serverIP = localaddr.getHostAddress();
        }
        catch (UnknownHostException e)
        {

             //System.out.println(GetErrorInfo.getErrorInfo(e, "WhoAmI"));
           e.printStackTrace();
        }
        return serverIP.toLowerCase();
    }

	public static void main(String[] args)
	{
        System.out.println("Hostname: " + getIP());
        System.out.println("Address: " + getIPAddress());
	}
}