package com.sp.socialcommerce.models;

import org.neo4j.graphdb.Label;

public class User implements Label{
	
	private String UID;
	private String signatureTimestamp;
	private String UIDSignature;
	
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

	@Override
	public String name() {
		return "User";
	}
}