package com.backend.services;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backend.entities.Produit;
import com.backend.exceptions.ConflictException;
import com.backend.exceptions.NotFoundException;
import com.backend.repositories.ProduitRepository;

@Service
public class ProduitService {
	
	@Autowired
	ProduitRepository rep;
	

	//Liste des produits
	public List<Produit> getProduits(Long id) throws NotFoundException
	{
		
		List<Produit> produits = new ArrayList<Produit>();
		
		if(id!=null) 
			{
				produits.add(rep.findById(id).orElseThrow(() -> new NotFoundException("Aucun produit avec l'id "+id+" n'existe")));
				return produits;
			}
		else
		{
			produits = rep.findAll();
			if(produits.isEmpty()) throw new NotFoundException("Aucun produit trouvé");		
			
			List<Produit> unique = produits.stream()
	                .collect(collectingAndThen(toCollection(() -> new TreeSet<>(comparing(Produit::getNom))),ArrayList::new));
			
			return unique;
		}
			
	}
	
	
	//Liste du meme produit dans differents stocks
		public List<Produit> getProduitsByNom(String nom) throws NotFoundException
		{
			
			List<Produit> produits = rep.findAllByNom(nom);
					if(produits.isEmpty()) throw new NotFoundException("Aucun produit trouvé");		
			
			return produits;
		}
	
	
	
	//Mettre à jour la quatite totale du meme produit dans differents stocks
		public void setQuantiteTotale(String nom)
		{
			List<Produit> produits = rep.findAllByNom(nom);
			if(produits.isEmpty()) throw new NotFoundException("Aucun produit avec le nom "+nom+" trouvé.");
			int quantiteTotale=0;
			for (Produit produit : produits) {
				quantiteTotale+=produit.getQuantiteEnStock();
			}
			
			for (Produit produit : produits) {
				produit.setQuantiteTotale(quantiteTotale);
				rep.save(produit);
			}
			
			
		}
		
	
	//ajouter un produit
	public void addProduit(Produit produit) throws ConflictException
	{
		if(rep.findByNom(produit.getNom()).isPresent()) 
			throw new ConflictException("Un produit avec la nom "+produit.getNom()+" existe déjà.");
		
		rep.save(produit);
		
		setQuantiteTotale(produit.getNom());
	}
	
	
	

	//modifier un produit
	public void updateProduit(Long id , Produit produit) throws ConflictException, NotFoundException
	{
		
		Produit updated=rep.findById(id)
				.orElseThrow(() -> new NotFoundException("Aucun produit avec l'id "+id+" n'existe"));
		
		if(rep.findByNom(produit.getNom()).isPresent() && !rep.findByNom(produit.getNom()).get().equals(updated))
			throw new ConflictException("Un produit avec la nom "+produit.getNom()+" existe déjà.");
		
		if(produit.getNom()!=null && !produit.getNom().isEmpty()) updated.setNom(produit.getNom());
		if(produit.getDescription()!=null && !produit.getDescription().isEmpty()) updated.setDescription(produit.getDescription());
		if(produit.getType()!=null && !produit.getType().isEmpty()) updated.setType(produit.getType());
		if(produit.getPrixAchat()!=0) updated.setPrixAchat(produit.getPrixAchat());
		if(produit.getQuantiteEnStock()!=0) updated.setQuantiteEnStock(produit.getQuantiteEnStock());
		if(produit.getQuantiteMin()!=0) updated.setQuantiteMin(produit.getQuantiteMin());
		if(produit.getQuantiteTotale()!=0) updated.setQuantiteTotale(produit.getQuantiteTotale());
		
		rep.save(updated);
		
		setQuantiteTotale(updated.getNom());
		
	}

	
	
	//supprimer un produit
	public void deleteProduit(Long id) throws NotFoundException
	{
		
		Produit produit= rep.findById(id)
				.orElseThrow(() -> new NotFoundException("Aucun produit avec l'id "+id+" n'existe"));
		rep.delete(produit);
		
		setQuantiteTotale(produit.getNom());
		
	}
	
	
	

}