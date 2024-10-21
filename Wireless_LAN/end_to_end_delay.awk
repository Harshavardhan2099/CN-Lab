BEGIN {
    total_delay = 0;
    received_packets = 0;
}
{
    if ($1 == "+" && $4 == "tcp") {  # When a TCP packet is sent
        send_time[$11] = $2;  # Record the time the packet was sent (packet ID is $11)
    } else if ($1 == "r" && $4 == "tcp") {  # When a TCP packet is received
        if (send_time[$11]) {
            delay = $2 - send_time[$11];  # Calculate delay for this packet
            total_delay += delay;
            received_packets++;
        }
    }
}
END {
    avg_delay = total_delay / received_packets;
    print "Average End-to-End Delay: " avg_delay " seconds";
}
