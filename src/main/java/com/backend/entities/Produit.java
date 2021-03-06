package com.backend.entities;



import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.*;

@Entity
@Table(name="PRODUIT")
public @Data class Produit {
		
		@Id
		@GeneratedValue(strategy=GenerationType.IDENTITY)
		@Column(name="ID_PROD")
		Long id;
		
		@Column(name="NOM_PROD")
		String nom;
		
		@Column(name="DESCRIPTION_PROD")
		String description;
		
		@Column(name="TYPE_PROD")
		String type;
		
		@Column(name="PRIX_ACHAT_PROD")
		double prixAchat;
		
		@Column(name="QUANTITE_EN_STOCK_PROD")
		int quantiteEnStock;
		
		@Column(name="QUANTITE_TOTALE_PROD")
		int quantiteTotale;
		
		@Column(name="QUANTITE_MIN_PROD")
		int quantiteMin;
		
		
		@JoinColumn(name="CATEGORIE_PROD")
		@ManyToOne
		Categorie categorie;
		
		@JoinColumn(name="FOURNISSEUR_PROD")
		@ManyToOne
		Fournisseur fournisseur;
		
		@JoinColumn(name="UNITE_DE_MESURE_PROD")
		@ManyToOne
		UniteDeMesure uniteDeMesure;
		
		@JoinColumn(name="Stock_PROD")
		@ManyToOne
		Stock stock;
		
		@JsonIgnore
		@Column(name="MOUVEMENTS_PROD")
		@OneToMany(mappedBy="produit", orphanRemoval= true)
		List<Mouvement> mouvements;

}
