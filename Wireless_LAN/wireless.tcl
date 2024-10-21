# Create a simulator object
set ns [new Simulator]

# Create trace and NAM files
set tracefile [open wireless.tr w]
$ns trace-all $tracefile
set namfile [open wireless.nam w]
$ns namtrace-all-wireless $namfile 500 500  ;# Setting grid size to 500x500

# Create topography and GOD (General Operations Director)
set topo [new Topography]
$topo load_flatgrid 500 500
create-god 6  ;# 6 nodes

# Create wireless channel
set channel1 [new Channel/WirelessChannel]

# Node configuration directly within node-config
$ns node-config -adhocRouting AODV \
    -llType LL \
    -macType Mac/802_11 \
    -ifqType Queue/DropTail/PriQueue \
    -ifqLen 50 \
    -antType Antenna/OmniAntenna \
    -propType Propagation/TwoRayGround \
    -phyType Phy/WirelessPhy \
    -topoInstance $topo \
    -agentTrace ON \
    -macTrace ON \
    -routerTrace ON \
    -movementTrace ON \
    -channel $channel1 

# Open a file for throughput data
set f0 [open throughput.txt w]

# Procedure to calculate throughput
proc record {} {
    global sink1 sink2 f0
    set ns [Simulator instance]
    set time 0.5
    set bw1 [$sink1 set bytes_]
    set bw2 [$sink2 set bytes_]
    set now [$ns now]
    puts $f0 "$now [expr ($bw1+$bw2)/$time*8/1000000]"  ;# Throughput in Mbps
    $sink1 set bytes_ 0
    $sink2 set bytes_ 0
    $ns at [expr $now+$time] "record"
}

# Create nodes using a loop
for {set i 0} {$i < 6} {incr i} {
    set n($i) [$ns node]
    $n($i) random-motion 0
}

# Set initial node positions using a list
set positions {
    {10 20} {210 230} {100 200} {150 230} {430 320} {270 120}
}

for {set i 0} {$i < 6} {incr i} {
    set pos [lindex $positions $i]
    $n($i) set X_ [lindex $pos 0]
    $n($i) set Y_ [lindex $pos 1]
    $n($i) set Z_ 0.0
}

# Define mobility for nodes
$ns at 1.0 "$n(1) setdest 490.0 340.0 25.0"
$ns at 1.0 "$n(4) setdest 300.0 130.0 5.0"
$ns at 1.0 "$n(5) setdest 190.0 440.0 15.0"
$ns at 20.0 "$n(5) setdest 100.0 200.0 30.0"

# Create TCP agents and attach them to nodes
set tcp [new Agent/TCP]
set sink1 [new Agent/LossMonitor]
$ns attach-agent $n(0) $tcp
$ns attach-agent $n(5) $sink1
$ns connect $tcp $sink1

# Attach FTP application to TCP agent
set ftp [new Application/FTP]
$ftp attach-agent $tcp

# Create a second TCP connection
set tcp1 [new Agent/TCP]
set sink2 [new Agent/LossMonitor]
$ns attach-agent $n(2) $tcp1
$ns attach-agent $n(3) $sink2
$ns connect $tcp1 $sink2

# Start the FTP application and record throughput
$ns at 0.0 "record"
$ns at 1.0 "$ftp start"

# Simulation termination
$ns at 30.0 "finish"

proc finish {} {
    global ns tracefile namfile f0
    $ns flush-trace
    close $tracefile
    close $namfile
    close $f0
    exit 0
}

# Start the simulation
puts "Starting Simulation..."
$ns run
