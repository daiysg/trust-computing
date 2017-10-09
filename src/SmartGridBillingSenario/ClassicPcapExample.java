package SmartGridBillingSenario;

/**
 * Created by ydai on 9/10/17.
 */

import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Here is the output generated by this example :
 * <p>
 * Network devices found:
 * #0: \Device\NPF_{BC81C4FC-242F-4F1C-9DAD-EA9523CC992D} [Intel(R) PRO/100 VE]
 * #1: \Device\NPF_{E048DA7F-D007-4EEF-909D-4238F6344971} [VMware Virtual Ethernet Adapter]
 * #2: \Device\NPF_{5B62B373-3EC1-460D-8C71-54AA0BF761C7} [VMware Virtual Ethernet Adapter]
 * #3: \Device\NPF_GenericDialupAdapter [Adapter for generic dialup and VPN capture]
 * <p>
 * Choosing 'Intel(R) PRO/100 VE) ' on your behalf:
 * Received packet at Tue Nov 03 18:52:42 EST 2009 caplen=1362 len=1362 jNetPcap rocks!
 * Received packet at Tue Nov 03 18:52:45 EST 2009 caplen=82   len=82   jNetPcap rocks!
 * Received packet at Tue Nov 03 18:52:45 EST 2009 caplen=145  len=145  jNetPcap rocks!
 * Received packet at Tue Nov 03 18:52:45 EST 2009 caplen=62   len=62   jNetPcap rocks!
 * Received packet at Tue Nov 03 18:52:45 EST 2009 caplen=164  len=164  jNetPcap rocks!
 * Received packet at Tue Nov 03 18:52:45 EST 2009 caplen=62   len=62   jNetPcap rocks!
 * Received packet at Tue Nov 03 18:52:45 EST 2009 caplen=54   len=54   jNetPcap rocks!
 * Received packet at Tue Nov 03 18:52:45 EST 2009 caplen=1073 len=1073 jNetPcap rocks!
 * Received packet at Tue Nov 03 18:52:45 EST 2009 caplen=1514 len=1514 jNetPcap rocks!
 * Received packet at Tue Nov 03 18:52:45 EST 2009 caplen=279  len=279  jNetPcap rocks!
 */
public class ClassicPcapExample {

    /**
     * Main startup method
     *
     * @param args ignored
     */
    public static void main(String[] args) {
        List<PcapIf> alldevs = new ArrayList<PcapIf>(); // Will be filled with NICs
        StringBuilder errbuf = new StringBuilder(); // For any error msgs

        /***************************************************************************
         * First get a list of devices on this system
         **************************************************************************/
        int r = Pcap.findAllDevs(alldevs, errbuf);
        if (r == Pcap.NOT_OK || alldevs.isEmpty()) {
            System.err.printf("Can't read list of devices, error is %s", errbuf
                    .toString());
            return;
        }

        System.out.println("Network devices found:");

        int i = 0;
        for (PcapIf device : alldevs) {
            String description =
                    (device.getDescription() != null) ? device.getDescription()
                            : "No description available";
            System.out.printf("#%d: %s [%s]\n", i++, device.getName(), description);
        }

        PcapIf device = alldevs.get(0); // We know we have atleast 1 device
        System.out
                .printf("\nChoosing '%s' on your behalf:\n",
                        (device.getDescription() != null) ? device.getDescription()
                                : device.getName());

        /***************************************************************************
         * Second we open up the selected device
         **************************************************************************/
        int snaplen = 64 * 1024;           // Capture all packets, no trucation
        int flags = Pcap.MODE_PROMISCUOUS; // capture all packets
        int timeout = 10 * 1000;           // 10 seconds in millis
        Pcap pcap =
                Pcap.openLive(device.getName(), snaplen, flags, timeout, errbuf);

        if (pcap == null) {
            System.err.printf("Error while opening device for capture: "
                    + errbuf.toString());
            return;
        }

        /***************************************************************************
         * Third we create a packet handler which will receive packets from the
         * libpcap loop.
         **************************************************************************/
        PcapPacketHandler<String> jpacketHandler = new PcapPacketHandler<String>() {

            public void nextPacket(PcapPacket packet, String user) {

                System.out.printf("Received packet at %s caplen=%-4d len=%-4d %s\n",
                        new Date(packet.getCaptureHeader().timestampInMillis()),
                        packet.getCaptureHeader().caplen(),  // Length actually captured
                        packet.getCaptureHeader().wirelen(), // Original length
                        user                                 // User supplied object
                );
            }
        };

        /***************************************************************************
         * Fourth we enter the loop and tell it to capture 10 packets. The loop
         * method does a mapping of pcap.datalink() DLT value to JProtocol ID, which
         * is needed by JScanner. The scanner scans the packet buffer and decodes
         * the headers. The mapping is done automatically, although a variation on
         * the loop method exists that allows the programmer to sepecify exactly
         * which protocol ID to use as the data link type for this pcap interface.
         **************************************************************************/
        pcap.loop(10, jpacketHandler, "jNetPcap rocks!");

        /***************************************************************************
         * Last thing to do is close the pcap handle
         **************************************************************************/
        pcap.close();
    }
}