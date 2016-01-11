package com.blogspot.sontx.iot.virtualclient;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.blogspot.sontx.iot.shared.utils.Convert;
import com.blogspot.sontx.iot.virtualclient.Client.OnStateChangedListener;

public class ClientUI extends JFrame implements ActionListener, OnStateChangedListener {
	private static final long serialVersionUID = 1L;
	private final Color onColor = Color.GREEN;
	private final Color offColor = Color.GRAY;
	private JPanel panel;
	private Client[] clients;

	public ClientUI(Client[] clients) {
		this.clients = clients;
		this.panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		for (Client client : clients) {
			JButton btn = new JButton(String.format("%d", client.getId()));
			btn.addActionListener(this);
			btn.setBackground(offColor);
			panel.add(btn);
			client.setOnStateChangedListener(this);
		}
		setMaximizedBounds(new Rectangle(500, 3000));
		add(panel);
		pack();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JButton btn = (JButton) e.getSource();
		int id = Convert.parseInt(btn.getText(), -1);
		for (Client client : clients) {
			if (client.getId() == id) {
				client.setState((byte) (client.getState() != 0 ? 0 : 1));
				break;
			}
		}
	}

	@Override
	public void stateChanged(Client client) {
		Component[] cmps = panel.getComponents();
		for (Component cmp : cmps) {
			JButton btn = (JButton) cmp;
			int id = Convert.parseInt(btn.getText(), -1);
			if (id == client.getId()) {
				cmp.setBackground(client.getState() != 0 ? onColor : offColor);
				break;
			}
		}
	}

}
