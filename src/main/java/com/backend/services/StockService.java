package com.backend.services;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.entities.Fournisseur;
import com.backend.entities.Inventaire;
import com.backend.entities.Mouvement;
import com.backend.entities.Produit;
import com.backend.entities.Stock;
import com.backend.entities.Utilisateur;
import com.backend.exceptions.ConflictException;
import com.backend.exceptions.NotFoundException;
import com.backend.repositories.StockRepository;

@Service
@Transactional
public class StockService {
	
	@Autowired
	StockRepository rep;
	
	@Autowired
	FournisseurService fournisseurService;
	
	@Autowired
	ProduitService produitService;
	
	@Autowired
	UtilisateurService utilisateurService;

	Logger logger = LoggerFactory.getLogger(StockService.class.getName());
	
	
	//Liste des stocks
	public List<Stock> getStocks(Long id) throws NotFoundException
	{
		List<Stock> stocks = new ArrayList<Stock>();
		if(id!=null) 
			stocks.add(rep.findById(id).orElseThrow(() -> new NotFoundException("Aucun stock avec l'id "+id+" n'existe")));
		
		else stocks = rep.findAll();
			if(stocks.isEmpty()) throw new NotFoundException("Aucun stock trouvé");		
		
		return stocks;
	}
	
	
	
	//Liste des produits
	public List<Produit> getProduits(Long id) throws NotFoundException
	{
		Stock stock = rep.findById(id)
				.orElseThrow(() -> new NotFoundException("Aucun stock avec l'id "+id+" n'existe"));
		
		List<Produit> produits = stock.getProduits();
		
		
		return produits;

	}
	
	
	
	//Trouver un produit par nom et prix achat
	public Produit produit(String nom, Fournisseur fournisseur, double prixAchat, Long id)
	{
		List<Produit> produits= getProduits(id);
		for (Produit produit : produits) {
			
			if(produit.getNom().equals(nom) && fournisseur.getId() == produit.getFournisseur().getId() && produit.getPrixAchat() == prixAchat)
			{
				return produit;
				
			}
		}
		
		return null;
	}
	
	
	
	//Liste des inventaires
		public List<Inventaire> getInventaires(Long id) throws NotFoundException
		{
			Stock stock = rep.findById(id)
					.orElseThrow(() -> new NotFoundException("Aucun stock avec l'id "+id+" n'existe"));
			
			List<Inventaire> inventaires = stock.getInventaires();
			
			
			return inventaires;

		}
		
		
		//Liste des mouvements
				public List<Mouvement> getMouvements(Long id) throws NotFoundException
				{
					Stock stock = rep.findById(id)
							.orElseThrow(() -> new NotFoundException("Aucun stock avec l'id "+id+" n'existe"));
					
					List<Mouvement> mouvements = stock.getMouvements();					
					
					return mouvements;

				}
		
	
	
	
	//ajouter un stock
	public void addStock(Stock stock) throws ConflictException
	{
		if(rep.findByEmplacement(stock.getEmplacement()).isPresent()) 
			throw new ConflictException("Un stock avec l'emplacement "+stock.getEmplacement().getDesignation()+" existe déjà.");
		
		rep.save(stock);
		
		
		
		
		Utilisateur user = utilisateurService.getByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
		logger.debug("L'utilisateur "+user.getNom()+" "+user.getPrenom()+" ayant le Username "+user.getUsername()+" a créé un stock à l'emplacement "+stock.getEmplacement().getDesignation());
		
	}
	
	
	

	//modifier un stock
	public void updateStock(Long id , Stock stock) throws ConflictException, NotFoundException
	{
		
		Stock updated=rep.findById(id)
				.orElseThrow(() -> new NotFoundException("Aucun stock avec l'id "+id+" n'existe"));
		
		if(rep.findByEmplacement(stock.getEmplacement()).isPresent() && !rep.findByEmplacement(stock.getEmplacement()).get().equals(updated))
			throw new ConflictException("Un stock avec l'emplacement "+stock.getEmplacement()+" existe déjà.");
		
		if(stock.getTelephone()!=null && !stock.getTelephone().isEmpty()) updated.setTelephone(stock.getTelephone());
		if(stock.getFax()!=null && !stock.getFax().isEmpty()) updated.setFax(stock.getFax());
		
		rep.save(updated);
		
		Utilisateur user = utilisateurService.getByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
		logger.debug("L'utilisateur "+user.getNom()+" "+user.getPrenom()+" ayant le Username "+user.getUsername()+" a modifié le stock "+updated.getEmplacement().getDesignation());
		
	}

	
	
	//supprimer un stock
	public void deleteStock(Long id) throws NotFoundException
	{
		
		Stock stock= rep.findById(id)
				.orElseThrow(() -> new NotFoundException("Aucun stock avec l'id "+id+" n'existe"));
		List<Produit> produits= stock.getProduits();
		if(!produits.isEmpty())
		{
			for (Produit produit : produits) {
				produitService.deleteProduit(produit.getId());
			}
		}
		rep.delete(stock);
		
		Utilisateur user = utilisateurService.getByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
		logger.debug("L'utilisateur "+user.getNom()+" "+user.getPrenom()+" ayant le Username "+user.getUsername()+" a supprimé le stock de l'emplacement "+stock.getEmplacement().getDesignation());
		
	}





}
