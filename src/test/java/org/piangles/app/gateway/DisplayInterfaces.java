package org.piangles.app.gateway;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

//http://checkip.amazonaws.com/
//http://icanhazip.com/
//http://www.trackip.net/ip
//http://myexternalip.com/raw
//http://ipecho.net/plain
public class DisplayInterfaces
{
        public static void main(String[] args) throws Exception
        {
                System.out.println("Host addr: " + InetAddress.getLocalHost().getHostAddress());  // often returns "127.0.0.1"
                Enumeration<NetworkInterface> n = NetworkInterface.getNetworkInterfaces();
                for (; n.hasMoreElements();)
                {
                        NetworkInterface e = n.nextElement();
                        System.out.println("Interface: " + e.getName());
                        Enumeration<InetAddress> a = e.getInetAddresses();
                        for (; a.hasMoreElements();)
                        {
                                InetAddress addr = a.nextElement();
                                System.out.println("  " + addr.getHostAddress());
                        }
                }
                
                System.out.println("getHost4Address :" + getHost4Address());
                System.out.println("getIp()" + getIp());
        }
        
        /**
         * Returns this host's non-loopback IPv4 addresses.
         * 
         * @return
         * @throws SocketException 
         */
        private static List<Inet4Address> getInet4Addresses() throws SocketException {
            List<Inet4Address> ret = new ArrayList<Inet4Address>();

            Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
            for (NetworkInterface netint : Collections.list(nets)) {
                Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
                for (InetAddress inetAddress : Collections.list(inetAddresses)) {
                    if (inetAddress instanceof Inet4Address && !inetAddress.isLoopbackAddress()) {
                        ret.add((Inet4Address)inetAddress);
                    }
                }
            }

            return ret;
        }

        /**
         * Returns this host's first non-loopback IPv4 address string in textual
         * representation.
         * 
         * @return
         * @throws SocketException
         */
        private static String getHost4Address() throws SocketException {
            List<Inet4Address> inet4 = getInet4Addresses();
            return !inet4.isEmpty()
                    ? inet4.get(0).getHostAddress()
                    : null;
        }
        
        public static String getIp() throws Exception {
            URL whatismyip = new URL("http://checkip.amazonaws.com");
            BufferedReader in = null;
            try {
                in = new BufferedReader(new InputStreamReader(
                        whatismyip.openStream()));
                String ip = in.readLine();
                return ip;
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
}