// Sender
var modem = getPeripheral("modem");
modem.openConnection(1);
modem.send("block.m")