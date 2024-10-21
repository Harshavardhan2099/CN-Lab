BEGIN {
    start_time = 0;
    end_time = 0;
    received_bytes = 0;
}
{
    if ($1 == "r" && $4 == "tcp") {   # Only consider received TCP packets
        if (start_time == 0) {
            start_time = $2;   # Set the start time to the time of the first received packet
        }
        end_time = $2;  # Update the end time as the time of the last received packet
        received_bytes += $6;  # Add up the packet size (in bytes)
    }
}
END {
    total_time = end_time - start_time;
    throughput = (received_bytes * 8) / (total_time * 1000000);  # Convert to Mbps
    print "Throughput: " throughput " Mbps";
}
