package cn.com.sinosure.mq.connection;

import com.rabbitmq.client.Connection;

public interface ConnectionListener {

	/**
	 * Called when a connection was established the first time.
	 * 
	 * @param connection
	 *            The established connection
	 */
	void onConnectionEstablished(Connection connection);

	/**
	 * Called when a connection was lost and the connection factory is trying to
	 * reestablish the connection.
	 * 
	 * @param connection
	 *            The lost connection
	 */
	void onConnectionLost(Connection connection);

	/**
	 * Called when a connection was ultimately closed and no new connection is
	 * going to be established in the future (this the case if the connection
	 * factory was teared down).
	 * 
	 * @param connection
	 *            The closed connection
	 */
	void onConnectionClosed(Connection connection);

}