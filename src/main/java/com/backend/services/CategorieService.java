package com.backend.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backend.entities.Categorie;
import com.backend.entities.Produit;
import com.backend.exceptions.ConflictException;
import com.backend.exceptions.NotFoundException;
import com.backend.repositories.CategorieRepository;

@Service
public class CategorieService {
	
	@Autowired
	CategorieRepository rep;
	

	//Liste des categories
	public List<Categorie> getCategories(Long id) throws NotFoundException
	{
		
		List<Categorie> categories = new ArrayList<Categorie>();
		if(id!=null) categories.add(rep.findById(id).orElseThrow(() -> new NotFoundException("Aucune catégorie trouvée avec l'id "+id)));
		else categories=rep.findAll();
			if(categories.isEmpty()) throw new NotFoundException("Aucune catégorie trouvée");
		
		return categories;
	}
	
	
	//Liste des produits
		public List<Produit> getProduits(Long id) throws NotFoundException
		{
			Categorie categorie = rep.findById(id).orElseThrow(() -> new NotFoundException("Aucune catégorie trouvée avec l'id "+id));		
			List<Produit> produits= categorie.getProduits();
			if(produits.isEmpty()) throw new NotFoundException("Aucun produit trouvé dans cette catégorie");
				
			return produits;
		}
	
	
	//ajouter une categorie
	public void addCategorie(Categorie categorie) throws ConflictException
	{
		if(rep.findByDesignation(categorie.getDesignation()).isPresent()) 
			throw new ConflictException("Une categorie avec la designation "+categorie.getDesignation()+" existe déjà.");
		
		rep.save(categorie);
	}
	
	
	

	//modifier une categorie
	public void updateCategorie(Long id , Categorie categorie) throws ConflictException, NotFoundException
	{
		
		Categorie updated=rep.findById(id)
				.orElseThrow(() -> new NotFoundException("Aucune catégorie avec l'id "+id+" n'existe"));
		
		if(rep.findByDesignation(categorie.getDesignation()).isPresent() && !rep.findByDesignation(categorie.getDesignation()).get().equals(updated))
			throw new ConflictException("Une categorie avec la designation "+categorie.getDesignation()+" existe déjà.");
		
		updated=categorie;
		updated.setId(id);
		
		rep.save(updated);
		
	}

	
	
	//supprimer une categorie
	public void deleteCategorie(Long id) throws NotFoundException
	{
		
		Categorie categorie= rep.findById(id)
				.orElseThrow(() -> new NotFoundException("Aucune catégorie avec l'id "+id+" n'existe"));
		rep.delete(categorie);
		
	}

}