package com.whoiszxl.core.common.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@AllArgsConstructor
@Data
public class RechargeResponse implements Serializable {

    private String address;

    private String qrCodeData;
}
