// create a simulator
set ns [new Simulator]

// create files for tracing
set nf [open namfile.nam w]
$ns namtrace-all $nf

set tf [open tracefile.tr w]
$ns trace-all $tf

// create nodes
set n0 [$ns node]
set n1 [$ns node]
set n2 [$ns node]
set n3 [$ns node]
set n4 [$ns node]
set n5 [$ns node]

// create links between them
$ns duplex-link $n0 $n1 5Mb 2ms DropTail
$ns duplex-link $n2 $n1 5Mb 2ms DropTail
$ns duplex-link $n1 $n4 5Mb 2ms DropTail
$ns duplex-link $n4 $n3 5Mb 2ms DropTail 
$ns duplex-link $n4 $n5 5Mb 2ms DropTail 

// tcp agent 
set tcp [new Agent/TCP]
set sink [new Agent/TCPSink]
$ns attach-agent $n0 $tcp
$ns attach-agent $n5 $sink
$ns connect $tcp $sink

// udp agent 
set udp [new Agent/UDP]
set null [new Agent/NUll]
$ns attach-agent $n1 $udp
$ns attach-agent $n3 $null
$ns connect $udp $null

// create traffic
set ftp [new Application/FTP]
$ftp attach-agent $tcp
set cbr [new Application/Traffic/CBR]
$cbr attach-agent $udp

// write a finish method 
proc finish {} {
global ns tf nf 
$ns flush-trace
close $nf 
close $tf
exit 0
}

// schedule the events
$ns at 1.0 "$ftp start"
$ns at 1.5 "$cbr start"
$ns at 5.0 "finish"

// start the simulation
$ns run