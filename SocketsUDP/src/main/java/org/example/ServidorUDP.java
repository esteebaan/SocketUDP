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

public class ServidorUDP extends JFrame {
    private JTextArea areaMensajes;
    private JTextField campoTexto;
    private DatagramSocket socketUDP;
    private byte[] bufferRecepcion = new byte[1024];
    private InetAddress direccionCliente; // Guardamos la dirección del cliente
    private int puertoCliente; // Guardamos el puerto del cliente

    public ServidorUDP() {
        // Configuración de la interfaz gráfica
        this.setTitle("Mini Chat Yeltsin");
        this.setSize(400, 300);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.areaMensajes = new JTextArea();
        this.areaMensajes.setEditable(false);
        this.campoTexto = new JTextField();
        this.add(new JScrollPane(this.areaMensajes), "Center");
        this.add(this.campoTexto, "South");
        this.campoTexto.setEnabled(false);

        this.campoTexto.addActionListener((e) -> {
            String mensaje = this.campoTexto.getText();
            if (direccionCliente != null) {
                enviarMensaje(mensaje);
                this.areaMensajes.append("Yeltsin: " + mensaje + "\n");
                this.campoTexto.setText("");
            } else {
                areaMensajes.append("Error: El cliente no esta conectado\n");
            }
        });

        this.setVisible(true);
        this.iniciarServidor();
    }

    private void iniciarServidor() {
        try {
            // Usamos el mismo puerto para enviar y recibir
            this.socketUDP = new DatagramSocket(5000, InetAddress.getByName("0.0.0.0"));
            this.areaMensajes.append("Servidor UDP iniciado en: " +
                    InetAddress.getLocalHost().getHostAddress() + ":5000\n");
            this.campoTexto.setEnabled(true);

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

                        this.areaMensajes.append("Joel: " + mensaje + "\n");

                        // Guardamos la dirección y puerto del cliente para responder
                        this.direccionCliente = paqueteRecepcion.getAddress();
                        this.puertoCliente = paqueteRecepcion.getPort();

                    } catch (IOException e) {
                        this.areaMensajes.append("Error en conexión: " + e.getMessage() + "\n");
                    }
                }
            }).start();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error en el servidor: " + e.getMessage());
        }
    }

    private void enviarMensaje(String mensaje) {
        try {
            byte[] bufferEnvio = mensaje.getBytes();

            // Usamos la dirección y puerto del cliente que guardamos
            DatagramPacket paqueteEnvio = new DatagramPacket(
                    bufferEnvio,
                    bufferEnvio.length,
                    direccionCliente,
                    puertoCliente);

            socketUDP.send(paqueteEnvio);
        } catch (IOException e) {
            areaMensajes.append("Error al enviar mensaje: " + e.getMessage() + "\n");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ServidorUDP::new);
    }
}
