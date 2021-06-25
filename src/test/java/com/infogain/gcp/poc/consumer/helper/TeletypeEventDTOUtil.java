package com.infogain.gcp.poc.consumer.helper;

import com.infogain.gcp.poc.consumer.dto.AddressLine;
import com.infogain.gcp.poc.consumer.dto.MessageBody;
import com.infogain.gcp.poc.consumer.dto.TeletypeEventDTO;

public class TeletypeEventDTOUtil {

    public static TeletypeEventDTO getDefaultTeletypeEventDTO() {

        String resourceLocator = "HDQ1S123";

        String[] messageBodyLine={"TRL",resourceLocator,"HDQEY AAABBC","1CUMBERBATCH/BENEDICTMR","EY042J20DEC DUBAUH HK1","OSI 1S THANK YOU FOR BOOKING ETIHAD"};

        MessageBody messageBody= new MessageBody();
        messageBody.setLine(messageBodyLine);
        AddressLine addressLine = new AddressLine();
        addressLine.setDestination("HDQRM1S");
        addressLine.setPriority("QD");

        TeletypeEventDTO teletypeEventDTO = new TeletypeEventDTO();
        teletypeEventDTO.setOrigin("HDQRMEY");
        teletypeEventDTO.setTimestamp("2019/06/10 09:32");
        teletypeEventDTO.setAddressLine(addressLine);
        teletypeEventDTO.setMessageCorrelationID("SABRE04P-1E95E72-96H5V87QC6BA2ROTQTOLO2M1INA6N7PHG8");
        teletypeEventDTO.setMessageIdentity("100932/38044891");
        teletypeEventDTO.setStandardMessageIdentifier("TRL");
        teletypeEventDTO.setMessageType("TEXT");
        teletypeEventDTO.setMessageBody(messageBody);

        return teletypeEventDTO;
    }
}
