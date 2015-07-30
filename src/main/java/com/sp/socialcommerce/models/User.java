package com.sp.socialcommerce.models;

import com.sp.socialcommerce.neo4j.GraphConstants;
import org.neo4j.graphdb.Label;

public class User implements Label{
	
	private String UID;
	private String signatureTimestamp;
	private String UIDSignature;
	private String userInfo;
	
	public String getUID() {
		return UID;
	}
	public void setUID(String UID) {
		this.UID = UID;
	}
	public String getSignatureTimestamp() {
		return signatureTimestamp;
	}
	public void setSignatureTimestamp(String signatureTimestamp) {
		this.signatureTimestamp = signatureTimestamp;
	}
	public String getUIDSignature() {
		return UIDSignature;
	}
	public void setUIDSignature(String UIDSignature) {
		this.UIDSignature = UIDSignature;
	}

	public String getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(String userInfo) {
		this.userInfo = userInfo;
	}

	@Override
	public String name() {
		return GraphConstants.User.USER_LABEL;
	}
}