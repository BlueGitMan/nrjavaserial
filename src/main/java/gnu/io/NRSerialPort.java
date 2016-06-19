package gnu.io;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.TooManyListenersException;

import gnu.io.factory.RxTxPortCreator;

public class NRSerialPort
{

    private RXTXPort serial;
    private String port = null;
    private boolean connected = false;
    private int baud = 115200;

    /**
     * Class Constructor for a NRSerialPort with a given port and baudrate.
     * 
     * @param port the port to connect to (i.e. COM6 or /dev/ttyUSB0)
     * @param baud the baudrate to use (i.e. 9600 or 115200)
     */
    public NRSerialPort(String port, int baud)
    {
    	setPort(port);
        setBaud(baud);
    }
    
    public boolean connect()
    {
        if (isConnected())
        {
            System.err.println(port + " is already connected.");
            return true;
        }

        try
        {
        	serial = new RxTxPortCreator().createPort(port);
            serial.setSerialPortParams(getBaud(), SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
            setConnected(true);
        }
        catch (NativeResourceException e)
        {
            throw new NativeResourceException(e.getMessage());
        }
        catch (Exception e)
        {
            System.err.println("Failed to connect on port: " + port + " exception: ");
            e.printStackTrace();
            setConnected(false);
        }

        if (isConnected())
        {
            serial.notifyOnDataAvailable(true);
        }
        return isConnected();
    }


    public InputStream getInputStream()
    {
		return serial.getInputStream();
    }


    public OutputStream getOutputStream()
    {
		return serial.getOutputStream();
    }


    /**
     * Set the port to use (i.e. COM6 or /dev/ttyUSB0)
     * 
     * @param port the serial port to use
     */
    private void setPort(String port)
    {
        this.port = port;
    }


    public void disconnect()
    {
        try
        {
            try
            {
                getInputStream().close();
                getOutputStream().close();
                serial.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
            serial = null;
            setConnected(false);
        }
        catch (UnsatisfiedLinkError e)
        {
            throw new NativeResourceException(e.getMessage());
        }
    }


    public static Set<String> getAvailableSerialPorts()
    {
        Set<String> available = new HashSet<String>();
        try
        {
            RXTXCommDriver d = new RXTXCommDriver();
            Set<String> av = d.getPortIdentifiers();
            ArrayList<String> strs = new ArrayList<String>();
            for (String s : av)
            {
                strs.add(0, s);
            }
            for (String s : strs)
            {
                available.add(s);
            }
        }
        catch (UnsatisfiedLinkError e)
        {
            e.printStackTrace();
            throw new NativeResourceException(e.getMessage());
        }

        return available;
    }


    public boolean isConnected()
    {
        return connected;
    }


    public void setConnected(boolean connected)
    {
        if (this.connected == connected)
            return;
        this.connected = connected;
    }


    public void setBaud(int baud)
    {

        this.baud = baud;
        return;

    }


    public int getBaud()
    {
        return baud;
    }


    public void notifyOnDataAvailable(boolean b)
    {
        serial.notifyOnDataAvailable(b);
    }


    public void addEventListener(SerialPortEventListener lsnr) throws TooManyListenersException
    {
        serial.addEventListener(lsnr);
    }


    public void removeEventListener()
    {
        serial.removeEventListener();
    }

    /**
     * Gets the {@link SerialPort} instance.
     * This will return null until {@link #connect()} is successfully called.
     * @return The {@link SerialPort} instance or null.
     */
    public RXTXPort getSerialPortInstance()
    {
        return serial;
    }

}
