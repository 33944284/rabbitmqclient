package cn.com.sinosure.mq.consumer;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.com.sinosure.mq.MQEnum;
import cn.com.sinosure.mq.config.MQPropertiesResolver;
import cn.com.sinosure.mq.connection.ConnectionListener;
import cn.com.sinosure.mq.connection.RabbitConnectionFactory;
import cn.com.sinosure.mq.connection.SingleConnectionFactory;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.ShutdownListener;

/**
 * A consumer container hosts consumers and manages their lifecycle.
 * 
 * @author christian.bick
 * @author uwe.janner
 * @author soner.dastan
 * 
 */
public class ConsumerContainer {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(ConsumerContainer.class);

	private static final int DEFAULT_AMOUNT_OF_INSTANCES = 1;

	private  Map<SingleConnectionFactory,ConnectionListener> conFactoryMap = new ConcurrentHashMap<SingleConnectionFactory,ConnectionListener>();
	
	Map<MQEnum, List<ConsumerHolder>> consumerHoldersMap = new ConcurrentHashMap<MQEnum, List<ConsumerHolder>>();
	
	List<ConsumerHolder> consumerHolders = Collections
			.synchronizedList(new LinkedList<ConsumerHolder>());

	private final Object activationMonitor = new Object();

	/**
	 * Creates the container using the given connection factory (re-)establish
	 * the connection to the broker.
	 * 
	 * @param connectionFactory
	 *            The connection factory
	 */
	public ConsumerContainer() {
//		super();
		
	}

	/**
	 * Adds a consumer to the container and binds it to the given queue with
	 * auto acknowledge disabled. Does NOT enable the consumer to consume from
	 * the message broker until the container is started.
	 * 
	 * @param consumer
	 *            The consumer
	 * @param queue
	 *            The queue to bind the consume to
	 */


	private void addConsumer(Consumer consumer, MQEnum businessType,int instances ) {
		addConsumer(consumer,businessType, new ConsumerConfiguration(businessType.getTargetQueue()),instances);
	}
	
	public void addConsumer(Consumer consumer, String businessKey,int instances ) {
		MQEnum businessType = MQPropertiesResolver.getMQProperties(businessKey);
		addConsumer(consumer,businessType, new ConsumerConfiguration(businessType.getTargetQueue()),instances);
	}
	
	public void addConsumer(Consumer consumer, String businessKey ) {
		MQEnum businessType = MQPropertiesResolver.getMQProperties(businessKey);
		addConsumer(consumer,businessType, new ConsumerConfiguration(businessType.getTargetQueue()),DEFAULT_AMOUNT_OF_INSTANCES);
	}
	
	public void addConsumer(Consumer consumer, String... businessKeys ) {
		for(String businessKey : businessKeys){
			this.addConsumer(consumer, businessKey);
		}
	}
	
	private void addConsumer(Consumer consumer, Map<MQEnum,Integer> instanceMap ) {
		Iterator<Entry<MQEnum,Integer>> iterator = instanceMap.entrySet().iterator();
		while(iterator.hasNext()){
			Entry<MQEnum,Integer> entry = iterator.next();
			addConsumer(consumer,entry.getKey(), new ConsumerConfiguration(entry.getKey().getTargetQueue()),entry.getValue());
		}
	}
	
	private void addConsumer(Consumer consumer, MQEnum... businessTypes  ) {
		for(MQEnum businessType : businessTypes){
			addConsumer(consumer,businessType, new ConsumerConfiguration(businessType.getTargetQueue()),DEFAULT_AMOUNT_OF_INSTANCES);
		}
	}

	

	/**
	 * Adds a consumer to the container and configures it according to the
	 * consumer configuration. Does NOT enable the consumer to consume from the
	 * message broker until the container is started.
	 * 
	 * <p>
	 * Registers the same consumer N times at the queue according to the number
	 * of specified instances. Use this for scaling your consumers locally. Be
	 * aware that the consumer implementation must be stateless or thread safe.
	 * </p>
	 * 
	 * @param consumer
	 *            The consumer
	 * @param configuration
	 *            The consumer configuration
	 * @param instances
	 *            the amount of consumer instances
	 */
	private synchronized void addConsumer(Consumer consumer,MQEnum type,
			ConsumerConfiguration configuration, int instances) {
		for (int i = 0; i < instances; i++) {
			ConsumerHolder consumerHolder = new ConsumerHolder(consumer, configuration,type);
			this.consumerHolders
					.add(consumerHolder);
			if(!consumerHoldersMap.containsKey(type)){
				List<ConsumerHolder> list =  Collections
						.synchronizedList(new LinkedList<ConsumerHolder>());

				consumerHoldersMap.put(type, list);
			}
			consumerHoldersMap.get(type).add(consumerHolder);
		}
		
	}

	/**
	 * <p>
	 * Starts all consumers managed by the container being an instance,
	 * extending or implementing the given class or interface.
	 * </p>
	 * 
	 * @see {@link #startAllConsumers()}
	 * 
	 * @param consumerClass
	 *            The consumer class or interface
	 * @throws IOException
	 *             if a consumer registration at the message broker fails
	 */
	private synchronized void startConsumers(
			Class<? extends Consumer> consumerClass) throws IOException {
		List<ConsumerHolder> consumerHolderSubList = filterConsumersForClass(consumerClass);
		enableConsumers(consumerHolderSubList);
	}

	/**
	 * <p>
	 * Starts all consumers managed by the container.
	 * </p>
	 * 
	 * <p>
	 * A started consumer consumes from the broker and is re-registered at the
	 * broker after a connection was lost and reestablished afterwards.
	 * </p>
	 * 
	 * @throws IOException
	 */
	public synchronized void startAllConsumers() throws IOException {
		enableConsumers(consumerHolders);
	}

	/**
	 * <p>
	 * Stops all consumers managed by the container being an instance, extending
	 * or implementing the given class or interface.
	 * </p>
	 * 
	 * @see {@link #stopAllConsumers()}
	 * 
	 * @param consumerClass
	 *            The consumer class or interface
	 */
	private synchronized void stopConsumers(
			Class<? extends Consumer> consumerClass) {
		List<ConsumerHolder> consumerHolderSubList = filterConsumersForClass(consumerClass);
		disableConsumers(consumerHolderSubList);
	}

	/**
	 * <p>
	 * Stops all consumers managed by the container.
	 * </p>
	 * 
	 * <p>
	 * A stopped consumer does not consume from the broker
	 * </p>
	 * 
	 */
	public synchronized void stopAllConsumers() {
		disableConsumers(consumerHolders);
	}

	/**
	 * Resets the container, stopping all consumers and removing them from the
	 * container.
	 */
	public synchronized void reset() {
		disableConsumers(consumerHolders);
		consumerHolders.clear();
	}

	/**
	 * <p>
	 * Gets all enabled consumers managed by the container.
	 * </p>
	 * 
	 * <p>
	 * Enabled consumers have been started once and not been stopped since that.
	 * An enabled consumer is (re-)registered by the container on connection
	 * (re-)establishing.
	 * </p>
	 * 
	 * @return The list of enabled consumers
	 */
	public List<ConsumerHolder> getEnabledConsumers() {
		return filterConsumersForEnabledFlag(true,null);
	}

	/**
	 * <p>
	 * Gets all disabled consumers managed by the container.
	 * </p>
	 * 
	 * <p>
	 * Disabled consumers have either never been started or have been stopped at
	 * some time and not been started again.
	 * </p>
	 * 
	 * @see {@link #getEnabledConsumers()}
	 * 
	 * @return The list of disabled consumers.
	 */
	public List<ConsumerHolder> getDisabledConsumers() {
		return filterConsumersForEnabledFlag(false,null);
	}

	/**
	 * <p>
	 * Gets all active consumers managed by the container
	 * </p>
	 * 
	 * <p>
	 * Active consumers must are also enabled consumers and are currently
	 * consuming from the broker. This means they have an active channel via an
	 * open connection to the broker and are registered at the broker.
	 * </p>
	 * 
	 * <p>
	 * Note: This method is only for use of information as it is not exact in
	 * terms of concurrency and time.
	 * </p>
	 * 
	 * @return The list of active consumers
	 */
	public List<ConsumerHolder> getActiveConsumers() {
		return filterConsumersForActiveFlag(true);
	}

	/**
	 * Gets all inactive consumers managed by the container</p>
	 * 
	 * <p>
	 * Inactive consumers may also be disabled consumers and are currently not
	 * consuming from the broker. In case a consumer is disabled this is an
	 * expected state. In case the consumer is enabled, the reason for the
	 * consumers inactivity is either a channel problem, a loss of connection or
	 * an unfinished registration at the broker.
	 * </p>
	 * 
	 * <p>
	 * Note: This method is only for use of information as it is not exact in
	 * terms of concurrency and time.
	 * </p>
	 * 
	 * @return The list of inactive consumers
	 */
	public List<ConsumerHolder> getInactiveConsumers() {
		return filterConsumersForActiveFlag(false);
	}

	/**
	 * Filters the consumers being an instance, extending or implementing the
	 * given class from the list of managed consumers.
	 * 
	 * @param consumerClass
	 *            The consumer class
	 * @return The filtered consumers
	 */
	protected List<ConsumerHolder> filterConsumersForClass(
			Class<? extends Consumer> consumerClass) {
		List<ConsumerHolder> consumerHolderSubList = new LinkedList<ConsumerHolder>();
		for (ConsumerHolder consumerHolder : consumerHolders) {
			if (consumerClass.isAssignableFrom(consumerHolder.getConsumer()
					.getClass())) {
				consumerHolderSubList.add(consumerHolder);
			}
		}
		return consumerHolderSubList;
	}

	/**
	 * Filters the consumers matching the given enabled flag from the list of
	 * managed consumers.
	 * 
	 * @param enabled
	 *            Whether to filter for enabled or disabled consumers
	 * @return The filtered consumers
	 */
	protected List<ConsumerHolder> filterConsumersForEnabledFlag(boolean enabled,MQEnum type) {
		List<ConsumerHolder> consumerHolderSubList = new LinkedList<ConsumerHolder>();
		for (ConsumerHolder consumerHolder : consumerHolders) {
			if (consumerHolder.isEnabled() == enabled ) {
				if(type == null || (type!=null && consumerHolder.getType() == type)){
					consumerHolderSubList.add(consumerHolder);
				}
			}
		}
		return consumerHolderSubList;
	}

	/**
	 * Filters the consumers matching the given active flag from the list of
	 * managed consumers.
	 * 
	 * @param active
	 *            Whether to filter for active or inactive consumers
	 * @return The filtered consumers
	 */
	protected List<ConsumerHolder> filterConsumersForActiveFlag(boolean active) {
		List<ConsumerHolder> consumerHolderSubList = new LinkedList<ConsumerHolder>();
		for (ConsumerHolder consumerHolder : consumerHolders) {
			if (consumerHolder.isActive() == active) {
				consumerHolderSubList.add(consumerHolder);
			}
		}
		return consumerHolderSubList;
	}

	/**
	 * Enables all consumers in the given list and hands them over for
	 * activation afterwards.
	 * 
	 * @param consumerHolders
	 *            The consumers to enable
	 * @throws IOException
	 *             if the activation process fails for a consumer
	 */
	protected void enableConsumers(List<ConsumerHolder> consumerHolders)
			throws IOException {
		checkPreconditions(consumerHolders);
		for (ConsumerHolder consumerHolder : consumerHolders) {
			consumerHolder.enable();
			// AsyncMessageProcessingConsumer processor = new
			// AsyncMessageProcessingConsumer(consumerHolder);
			// this.executor.execute(processor);
			// Thread thread1 = new Thread(processor);
			// thread1.start();
		}
	}

	/**
	 * Disables all consumers in the given list after deactivating them.
	 * 
	 * @param consumerHolders
	 *            The consumers to disable
	 */
	protected void disableConsumers(List<ConsumerHolder> consumerHolders) {
		for (ConsumerHolder consumerHolder : consumerHolders) {
			consumerHolder.disable();
		}
	}

	/**
	 * Activates all consumers in the given list.
	 * 
	 * @param consumerHolders
	 *            The list of consumers to activate
	 * @throws IOException
	 *             if the activation process fails for a consumer
	 */
	protected void activateConsumers(List<ConsumerHolder> consumerHolders)
			throws IOException {
		synchronized (activationMonitor) {
			for (ConsumerHolder consumerHolder : consumerHolders) {
				try {
					consumerHolder.activate();
				} catch (IOException e) {
					LOGGER.error("Failed to activate consumer - deactivating already activated consumers");
					deactivateConsumers(consumerHolders);
					throw e;
				}
			}
		}
	}

	/**
	 * Deactivates all consumers in the given list.
	 * 
	 * @param consumerHolders
	 *            The list of consumers to deactivate.
	 */
	protected void deactivateConsumers(List<ConsumerHolder> consumerHolders) {
		synchronized (activationMonitor) {
			for (ConsumerHolder consumerHolder : consumerHolders) {
				consumerHolder.deactivate();
			}
		}
	}

	/**
	 * Checks if all preconditions are fulfilled on the broker to successfully
	 * register a consumer there. One important precondition is the existence of
	 * the queue the consumer shall consume from.
	 * 
	 * @param consumerHolders
	 *            The consumer holders
	 * @throws IOException
	 *             if the precondition check fails
	 */
	protected void checkPreconditions(List<ConsumerHolder> consumerHolders)
			throws IOException {
		
		
		for (ConsumerHolder consumerHolder : consumerHolders) {
			Channel channel = createChannel(consumerHolder.getType());
			String queue = consumerHolder.getConfiguration().getQueueName();
			try {
				channel.queueDeclarePassive(queue);
				LOGGER.debug("Queue {} found on broker", queue);
			} catch (IOException e) {
				LOGGER.error("Queue {} not found on broker", queue);
				throw e;
			}
			channel.close();
		}
		
	}

	
	
	/**
	 * Creates a channel to be used for consuming from the broker.
	 * 
	 * @return The channel
	 * @throws IOException
	 *             if the channel cannot be created due to a connection problem
	 */
	protected Channel createChannel(MQEnum businessType) throws IOException {
		LOGGER.debug("Creating channel");
		SingleConnectionFactory conFactory = RabbitConnectionFactory.getConnectionFactory(businessType.getVhost(), businessType.getUser(), businessType.getPassword());
		if(!conFactoryMap.containsKey(conFactory)){
			SingleConnectionListener singleListener = new SingleConnectionListener(businessType);
			conFactory.registerListener(singleListener);
			conFactoryMap.put(conFactory, singleListener);
		}
		
		Connection connection = conFactory.newConnection();
		Channel channel = connection.createChannel();
		LOGGER.debug("Created channel");
		return channel;
	}

	/**
	 * A container connection listener to react on events of a
	 * {@link SingleConnectionFactory} if used.
	 * 
	 * @author christian.bick
	 * 
	 */
	protected class SingleConnectionListener implements ConnectionListener {

		private MQEnum type;
		
		SingleConnectionListener(MQEnum type){
			this.type = type;
		}
		@Override
		public void onConnectionEstablished(Connection connection) {
			String hostName = connection.getAddress().getHostName();
			LOGGER.info("Connection established to {}", hostName);
			List<ConsumerHolder> enabledConsumerHolders = filterConsumersForEnabledFlag(true,type);
			LOGGER.info("Activating {} enabled consumers",
					enabledConsumerHolders.size());
			try {
				activateConsumers(enabledConsumerHolders);
				LOGGER.info("Activated enabled consumers");
			} catch (IOException e) {
				LOGGER.error("Failed to activate enabled consumers", e);
				deactivateConsumers(enabledConsumerHolders);
			}
		}

		@Override
		public void onConnectionLost(Connection connection) {
			LOGGER.warn("Connection lost");
			LOGGER.info("Deactivating enabled consumers");
			List<ConsumerHolder> enabledConsumerHolders = filterConsumersForEnabledFlag(true,type);
			deactivateConsumers(enabledConsumerHolders);
		}

		@Override
		public void onConnectionClosed(Connection connection) {
			LOGGER.warn("Connection closed for ever");
			LOGGER.info("Deactivating enabled consumers");
			List<ConsumerHolder> enabledConsumerHolders = filterConsumersForEnabledFlag(true,type);
			deactivateConsumers(enabledConsumerHolders);
		}
	}

	private class AsyncMessageProcessingConsumer implements Runnable {

		private final ConsumerHolder consumerHolder;

		private final CountDownLatch start;

		public AsyncMessageProcessingConsumer(ConsumerHolder consumerHolder) {
			this.consumerHolder = consumerHolder;
			this.start = new CountDownLatch(1);
		}

		@Override
		public void run() {

			try {
				this.consumerHolder.enable();
				this.start.countDown();
				System.out.println("thread==" + Thread.currentThread().getId());
			} catch (IOException e) {
				LOGGER.error("Consumer thread Throwable, thread Throwable.", e);
			} finally {
				this.start.countDown();
			}
		}

	}

	/**
	 * A holder of a consumer attaching additional state to the consumer.
	 * 
	 * @author christian.bick
	 * 
	 */
	public class ConsumerHolder {

		Channel channel;
		Consumer consumer;
		ConsumerConfiguration configuration;
		ShutdownListener channelShutdownListener;

		MQEnum type;
		public MQEnum getType() {
			return type;
		}

		boolean enabled = false;
		boolean active = false;

		public ConsumerHolder(Consumer consumer,
				ConsumerConfiguration configuration,MQEnum type) {
			this.consumer = consumer;
			this.configuration = configuration;
			this.type = type;
			if (consumer instanceof ManagedConsumer) {
				((ManagedConsumer) consumer).setConfiguration(configuration);
			}
		}

		public Consumer getConsumer() {
			return consumer;
		}

		public ConsumerConfiguration getConfiguration() {
			return configuration;
		}

		public boolean isEnabled() {
			return enabled;
		}

		public boolean isActive() {
			return active;
		}

		public void enable() throws IOException {
			enabled = true;
			activate();
		}

		public void disable() {
			enabled = false;
			deactivate();
		}

		void deactivate() {
			LOGGER.info("Deactivating consumer of class {}",
					consumer.getClass());
			if (channel != null) {
				try {
					LOGGER.info("Closing channel for consumer of class {}",
							consumer.getClass());
					channel.close();
					LOGGER.info("Closed channel for consumer of class {}",
							consumer.getClass());
				} catch (Exception e) {
					LOGGER.info(
							"Aborted closing channel for consumer of class {} (already closing)",
							consumer.getClass());
					// Ignore exception: In this case the channel is for sure
					// not usable any more
				}
				channel = null;
			}
			active = false;
			LOGGER.info("Deactivated consumer of class {}", consumer.getClass());
		}

		void activate() throws IOException {
			LOGGER.info("Activating consumer of class {}", consumer.getClass());
			// Make sure the consumer is not active before starting it
			if (isActive()) {
				deactivate();
			}
			// Start the consumer
			try {
				channel = createChannel(type);
				if (consumer instanceof ManagedConsumer) {
					((ManagedConsumer) consumer).setChannel(channel);
				}
				channel.basicQos(configuration.getPrefetchMessageCount());

				channel.basicConsume(configuration.getQueueName(),
						configuration.isAutoAck(), consumer);

				active = true;

				LOGGER.info("Activated consumer of class {}" + this.toString(),
						consumer.getClass());
			} catch (IOException e) {
				LOGGER.error("Failed to activate consumer of class {}",
						consumer.getClass(), e);
				throw e;
			}
		}
	}

	public static abstract class ManagedConsumer implements Consumer {

		private Channel channel;
		private ConsumerConfiguration configuration;

		void setChannel(Channel channel) {
			this.channel = channel;
		}

		protected Channel getChannel() {
			return channel;
		}

		void setConfiguration(ConsumerConfiguration configuration) {
			this.configuration = configuration;
		}

		protected ConsumerConfiguration getConfiguration() {
			return configuration;
		}
	}

}
