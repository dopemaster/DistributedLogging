package edu.dsy.mp1;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 
 * Base Class for the server.
 * 
 */
public abstract class ServerListener {
	ServerSocket sSocket;
	Socket connection = null;
	ObjectOutputStream out;
	ObjectInputStream in;
	InputParameters input;

	DocumentBuilderFactory dbFactory;
	DocumentBuilder dBuilder;
	Document doc;
	NodeList nList;
	String fileXML;

	/**
	 * 
	 * @param fileXML
	 */
	public ServerListener(String fileXML) {
		this.fileXML = fileXML;
	}

	/**
 * 
 */
	public void run() {

		int port = 0;
		File propertiesXML = new File(fileXML);
		try {

			dbFactory = DocumentBuilderFactory.newInstance();
			dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(propertiesXML);
			doc.getDocumentElement().normalize();

			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath = factory.newXPath();
			XPathExpression expr = xpath.compile("//servers/server[name='"
					+ InetAddress.getLocalHost().getHostName()
					+ "']/port/text()");

			Object result = expr.evaluate(doc, XPathConstants.NODE);
			Node n = (Node) result;
			port = Integer.parseInt(n.getNodeValue());
		} catch (Exception e) {
			e.printStackTrace();
		}

		while (true) {
			try {
				sSocket = new ServerSocket(port);
				connection = sSocket.accept();
				System.out.println("Connection received from "
						+ connection.getInetAddress().getHostName());
				out = new ObjectOutputStream(connection.getOutputStream());
				out.flush();
				in = new ObjectInputStream(connection.getInputStream());
				try {
					input = (InputParameters) in.readObject();
					doAction(input);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} finally {
					in.close();
					out.close();
					sSocket.close();
				}
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}
	}

	/**
	 * 
	 * @param msg
	 */
	public void sendMessage(String msg) {
		try {
			out.writeObject(msg);
			out.flush();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}

	/**
	 * 
	 * @param input
	 * @throws IOException
	 */
	public void doAction(InputParameters input) throws IOException {
		return;
	}
}
