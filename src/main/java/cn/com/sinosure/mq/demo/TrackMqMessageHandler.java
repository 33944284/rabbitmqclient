package cn.com.sinosure.mq.demo;

import java.io.IOException;

import javax.inject.Named;

import cn.com.sinosure.mq.consumer.AmqpMessage;
import cn.com.sinosure.mq.utils.spring.MessageConsumerAdapter;
//@Named
public class TrackMqMessageHandler extends MessageConsumerAdapter { // ע��̳�MessageConsumerAdapater
                                                                    // ��

    public void handleMessage(AmqpMessage message) throws IOException {

        String bodyJSONStr = (String) message
                .getMessageBodyObject(String.class); // ע�ⷢ���������Լ������ݶ�������

        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@json Str ==="
                + bodyJSONStr);
    }

    public String getRabbitKey() {// //ָ��������߾��崦���Ǹ��������Ϣ
        // TODO Auto-generated method stub
        return "rabbit.milTrackItem"; // �����ļ��������key
    }

	
	public boolean isAutoAck() {
		// TODO Auto-generated method stub
		return false;
	}

}
