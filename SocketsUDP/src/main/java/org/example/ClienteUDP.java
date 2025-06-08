package org.example;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class ClienteUDP extends JFrame {
    private JTextField campoTexto;
    private JTextArea areaMensajes;
    private DatagramSocket socketUDP;
    private byte[] bufferRecepcion = new byte[1024];
    private InetAddress direccionServidor; // Dirección del servidor
    private int puertoServidor = 5000; // Puerto del servidor

    public ClienteUDP() {
        // Configuración de la interfaz gráfica
        this.setTitle("Mini Chat Joel");
        this.setSize(400, 300);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.campoTexto = new JTextField();
        this.areaMensajes = new JTextArea();
        this.areaMensajes.setEditable(false);
        this.add(this.campoTexto, "South");
        this.add(new JScrollPane(this.areaMensajes), "Center");

        this.campoTexto.addActionListener((e) -> {
            String mensaje = this.campoTexto.getText();
            enviarMensaje(mensaje);
            this.areaMensajes.append("Joel: " + mensaje + "\n");
            this.campoTexto.setText("");
        });

        this.setVisible(true);
        this.conectarAlServidor();
    }

    private void conectarAlServidor() {
        try {
            // El cliente usa un puerto aleatorio (0) o puedes especificar uno
            this.socketUDP = new DatagramSocket(0);
            this.direccionServidor = InetAddress.getByName("192.168.0.3");

            // Mensaje inicial para que el servidor conozca nuestra dirección
            enviarMensaje("Conexion iniciada");

            new Thread(() -> {
                while (true) {
                    try {
                        DatagramPacket paqueteRecepcion = new DatagramPacket(
                                bufferRecepcion, bufferRecepcion.length);

                        socketUDP.receive(paqueteRecepcion);

                        String mensaje = new String(
                                paqueteRecepcion.getData(),
                                0,
                                paqueteRecepcion.getLength());

                        // Ignoramos el mensaje de conexión inicial
                        if (!mensaje.equals("--CONEXION INICIADA--")) {
                            this.areaMensajes.append("Yeltsin: " + mensaje + "\n");
                        }
                    } catch (IOException e) {
                        this.areaMensajes.append("Error de conexión: " + e.getMessage() + "\n");
                    }
                }
            }).start();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "No se pudo conectar al servidor: " + e.getMessage());
        }
    }

    private void enviarMensaje(String mensaje) {
        try {
            byte[] bufferEnvio = mensaje.getBytes();

            DatagramPacket paqueteEnvio = new DatagramPacket(
                    bufferEnvio,
                    bufferEnvio.length,
                    direccionServidor,
                    puertoServidor);

            socketUDP.send(paqueteEnvio);
        } catch (IOException e) {
            areaMensajes.append("Error al enviar mensaje: " + e.getMessage() + "\n");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ClienteUDP::new);
    }
}