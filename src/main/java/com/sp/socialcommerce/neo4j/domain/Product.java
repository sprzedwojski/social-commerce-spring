package com.sp.socialcommerce.neo4j.domain;

import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;

@NodeEntity
public class Product {
	@GraphId
	Long id;
	private String name;
	private String imageUrl;
	private String description;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean equals(Object other) {

		if (this == other)
			return true;

		if (id == null)
			return false;

		if (!(other instanceof Product))
			return false;

		return id.equals(((Product) other).id);
	}

	public int hashCode() {
		return id == null ? System.identityHashCode(this) : id.hashCode();
	}

	public String toString() {
		return "Product[id:" + id + ",name:" + name + ",description:" + description + "]";
	}

}
