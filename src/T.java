import java.text.SimpleDateFormat;
import java.util.Date;

/*
***************************************************************************
**          WiseStone Co. Ltd. CONFIDENTIAL AND PROPRIETARY
**        This source is the sole property of WiseStone Co. Ltd.
**      Reproduction or utilization of this source in whole or in part 
**    is forbidden without the written consent of WiseStone Co. Ltd.
***************************************************************************
**                 Copyright (c) 2007 WiseStone Co. Ltd.
**                           All Rights Reserved
***************************************************************************
** Revision History:
** Author                 Date          Version      Description of Changes
** ------------------------------------------------------------------------
** dhwoo     2010. 3. 12.        1.0              Created
*/

public class T
{
//	private final static String PREFIX = "LogFilter";
	private final static String POSTFIX = "[iookill]";
	private static Boolean misEnabled = true;

	public static void enable( Boolean isEnable )
	{
		misEnabled = isEnable;
	}

    public static void e()
    {
        if ( misEnabled )
        {
            Exception e = new Exception();
            StackTraceElement callerElement = e.getStackTrace()[1];
            System.out.println( getCurrentTime() + 
                   POSTFIX + "[" +
                   callerElement.getFileName() + ":" +
                   callerElement.getMethodName() + ":" + 
                   callerElement.getLineNumber() + "]");
        }
    }

    public static void e( Object strMsg )
    {
        if ( misEnabled )
        {
            Exception e = new Exception();
            StackTraceElement callerElement = e.getStackTrace()[1];
            System.out.println( getCurrentTime() + 
                   POSTFIX + "[" +
                   callerElement.getFileName() + ":" +
                   callerElement.getMethodName() + ":" + 
                   callerElement.getLineNumber() + "]" +
                   strMsg );
        }
    }

    public static void w()
    {
        if ( misEnabled )
        {
            Exception e = new Exception();
            StackTraceElement callerElement = e.getStackTrace()[1];
            System.out.println( getCurrentTime() + 
                   POSTFIX + "[" +
                   callerElement.getFileName() + ":" +
                   callerElement.getMethodName() + ":" + 
                   callerElement.getLineNumber() + "]");
        }
    }

	public static void w( Object strMsg )
	{
		if ( misEnabled )
		{
			Exception e = new Exception();
			StackTraceElement callerElement = e.getStackTrace()[1];
            System.out.println( getCurrentTime() + 
				   POSTFIX + "[" +
				   callerElement.getFileName() + ":" +
				   callerElement.getMethodName() + ":" + 
				   callerElement.getLineNumber() + "]" +
				   strMsg );
		}
	}

	public static void i()
	{
		if ( misEnabled )
		{
			Exception e = new Exception();
			StackTraceElement callerElement = e.getStackTrace()[1];
            System.out.println( getCurrentTime() + 
				   POSTFIX + "[" +
				   callerElement.getFileName() + ":" +
				   callerElement.getMethodName() + ":" + 
				   callerElement.getLineNumber() + "]");
		}
	}

    public static void i( Object strMsg )
    {
        if ( misEnabled )
        {
            Exception e = new Exception();
            StackTraceElement callerElement = e.getStackTrace()[1];
            System.out.println( getCurrentTime() + 
                   POSTFIX + "[" +
                   callerElement.getFileName() + ":" +
                   callerElement.getMethodName() + ":" + 
                   callerElement.getLineNumber() + "]" +
                   strMsg );
        }
    }

    public static void d()
	{
		if ( misEnabled )
		{
			Exception e = new Exception();
			StackTraceElement callerElement = e.getStackTrace()[1];
            System.out.println( getCurrentTime() + 
				   POSTFIX + "[" +
				   callerElement.getFileName() + ":" +
				   callerElement.getMethodName() + ":" + 
				   callerElement.getLineNumber() + "]");
		}
	}

    public static void d( Object strMsg )
    {
        if ( misEnabled )
        {
            Exception e = new Exception();
            StackTraceElement callerElement = e.getStackTrace()[1];
            System.out.println( getCurrentTime() + 
                   POSTFIX + "[" +
                   callerElement.getFileName() + ":" +
                   callerElement.getMethodName() + ":" + 
                   callerElement.getLineNumber() + "]" +
                   strMsg );
        }
    }
    
    public static String getCurrentTime()
    {
        long time = System.currentTimeMillis(); 

        SimpleDateFormat dayTime = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss.SSS");

        return dayTime.format(new Date(time));

    }
}
