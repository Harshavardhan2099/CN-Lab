BEGIN {
    sent_packets = 0;
    received_packets = 0;
}
{
    if ($1 == "+" && $4 == "tcp") {  # Count TCP packets that are sent
        sent_packets++;
    } else if ($1 == "r" && $4 == "tcp") {  # Count TCP packets that are received
        received_packets++;
    }
}
END {
    packet_loss = sent_packets - received_packets;
    print "Sent Packets: " sent_packets;
    print "Received Packets: " received_packets;
    print "Packet Loss: " packet_loss;
}
