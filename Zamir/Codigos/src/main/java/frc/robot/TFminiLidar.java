package frc.robot;

import edu.wpi.first.wpilibj.SerialPort;

public class TFminiLidar {

    private static final int BAUD_RATE = 9600;
    private SerialPort serial;
    private boolean conectado = false;
    private double distancia_mm = 0.0;
    private String portaUsada = "NENHUMA";
    private int readCount = 0;
    private byte[] buffer = new byte[50];
    private int bufferIndex = 0;

    public TFminiLidar() {
        SerialPort.Port[] portas = {
            SerialPort.Port.kUSB,
            SerialPort.Port.kUSB1,
            SerialPort.Port.kUSB2,
            SerialPort.Port.kMXP,
            SerialPort.Port.kOnboard
        };

        for (SerialPort.Port porta : portas) {
            try {
                System.out.println("Tentando TFmini em porta: " + porta);
                serial = new SerialPort(BAUD_RATE, porta);
                serial.setReadBufferSize(256);
                serial.setWriteBufferSize(256);
                serial.setTimeout(0.01);
                conectado = true;
                portaUsada = porta.toString();
                System.out.println("TFmini conectado em: " + porta);
                return;
            } catch (Exception e) {
                System.out.println("TFmini falhou em " + porta + ": " + e.getMessage());
                serial = null;
            }
        }

        System.out.println("TFmini: nenhuma porta funcionou!");
        conectado = false;
    }

    public void readDistance() {
        if (!conectado || serial == null) return;

        try {
            byte[] chunk = serial.read(32);
            if (chunk.length == 0) return;
            
            for (byte b : chunk) {
                buffer[bufferIndex] = b;
                bufferIndex++;
                
                if (bufferIndex >= buffer.length) {
                    bufferIndex = 0;
                }
                
                if (bufferIndex >= 9) {
                    int checkIdx = bufferIndex - 9;
                    if (checkIdx < 0) checkIdx += buffer.length;
                    
                    if ((buffer[checkIdx] & 0xFF) == 0x59 && 
                        (buffer[(checkIdx + 1) % buffer.length] & 0xFF) == 0x59) {
                        
                        byte b2 = buffer[(checkIdx + 2) % buffer.length];
                        byte b3 = buffer[(checkIdx + 3) % buffer.length];
                        
                        int dist = ((b3 & 0xFF) << 8) | (b2 & 0xFF);
                        
                        if (dist > 0 && dist < 12000) {
                            distancia_mm = dist;
                            readCount++;
                            if (readCount % 30 == 0) {
                                System.out.println("TFmini: " + dist + " mm");
                            }
                        }
                    }
                }
            }
            
        } catch (Exception e) {
            System.out.println("TFmini read erro: " + e.getMessage());
        }
    }

    public double getDistancia_mm() {
        return distancia_mm;
    }

    public double getDistancia_m() {
        return distancia_mm / 1000.0;
    }

    public boolean isConectado() {
        return conectado;
    }

    public String getPortaUsada() {
        return portaUsada;
    }
    public int getReadCount() {
        return readCount;
    }
}