package com.backend.services;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.backend.entities.Categorie;
import com.backend.entities.Produit;
import com.backend.entities.UniteDeMesure;
import com.backend.entities.Utilisateur;
import com.backend.exceptions.ConflictException;
import com.backend.exceptions.NotFoundException;
import com.backend.repositories.UniteDeMesureRepository;


@Service
public class UniteDeMesureService {
	
	@Autowired
	UniteDeMesureRepository rep;
	
	@Autowired
	UtilisateurService utilisateurService;
	
	Logger logger = LoggerFactory.getLogger(UniteDeMesureService.class.getName());
	

	//Liste des unités de mesure
	public List<UniteDeMesure> getUniteDeMesures(Long id) throws NotFoundException
	{
		
		List<UniteDeMesure> uniteDeMesures = new ArrayList<UniteDeMesure>();
		if(id!=null) uniteDeMesures.add(rep.findById(id).orElseThrow(()-> new NotFoundException("Aucune unité de mesure avec l'id "+id+" n'existe")));
		else uniteDeMesures=rep.findAll();
			if(uniteDeMesures.isEmpty()) throw new NotFoundException("Aucune unité de mesure trouvée");		
		
		return uniteDeMesures;
	}
	
	
	//Liste des produits
	public List<Produit> getProduits(Long id) throws NotFoundException
	{
		
		UniteDeMesure uniteDeMesure= rep.findById(id)
				.orElseThrow(()-> new NotFoundException("Aucune unité de mesure avec l'id "+id+" n'existe"));
		
		List<Produit> produits=uniteDeMesure.getProduits();
		
		if(produits.isEmpty())
			throw new NotFoundException("Aucun produit ayant cette unité de mesure.");
		
		List<Produit> unique = produits.stream()
                .collect(collectingAndThen(toCollection(() -> new TreeSet<>(comparing(Produit::getNom))),ArrayList::new));
		
		return unique;
		
		
	}
	
	
	//ajouter une unité de mesure
	public void addUniteDeMesure(UniteDeMesure uniteDeMesure) throws ConflictException
	{
		if(rep.findByDesignation(uniteDeMesure.getDesignation()).isPresent()) 
			throw new ConflictException("Une unité de mesure avec la designation "+uniteDeMesure.getDesignation()+" existe déjà.");
		
		rep.save(uniteDeMesure);
		
		Utilisateur user = utilisateurService.getByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
			logger.debug("L'administrateur "+user.getNom()+" "+user.getPrenom()+" ayant le Username "+user.getUsername()+" a créé l'unité de mesure "+uniteDeMesure.getDesignation());
		
	}
	
	
	

	//modifier une unité de mesure
	public void updateUniteDeMesure(Long id , UniteDeMesure uniteDeMesure) throws ConflictException, NotFoundException
	{
		
		UniteDeMesure updated=rep.findById(id)
				.orElseThrow(() -> new NotFoundException("Aucune unité de mesure avec l'id "+id+" n'existe"));
		
		if(rep.findByDesignation(uniteDeMesure.getDesignation()).isPresent() && !rep.findByDesignation(uniteDeMesure.getDesignation()).get().equals(updated))
			throw new ConflictException("Une unité de mesure avec la designation "+uniteDeMesure.getDesignation()+" existe déjà.");
		
		if(uniteDeMesure.getDesignation()!=null && !uniteDeMesure.getDesignation().isEmpty()) updated.setDesignation(uniteDeMesure.getDesignation());
		if(uniteDeMesure.getDescription()!=null && !uniteDeMesure.getDescription().isEmpty()) updated.setDescription(uniteDeMesure.getDescription());
		
		rep.save(updated);
		
		Utilisateur user = utilisateurService.getByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
		logger.debug("L'administrateur "+user.getNom()+" "+user.getPrenom()+" ayant le Username "+user.getUsername()+" a modifié l'unité de mesure "+updated.getDesignation());
		
	}

	
	
	//supprimer une unité de mesure
	public void deleteUniteDeMesure(Long id) throws NotFoundException
	{
		
		UniteDeMesure uniteDeMesure= rep.findById(id)
				.orElseThrow(() -> new NotFoundException("Aucune unité de mesure avec l'id "+id+" n'existe"));
		boolean aucunProduit = false;
		try
		{
			List<Produit> produits = getProduits(id);
		}catch(NotFoundException e)
		{
			aucunProduit = true;
		}
		if(aucunProduit==false)
		{
		List<Produit> produits = getProduits(id);
		UniteDeMesure inconnue = rep.findByDesignation("Non spécifiée").get();
		for (Produit produit : produits) {
			produit.setUniteDeMesure(inconnue);
		}
		uniteDeMesure.setProduits(null);
		rep.save(uniteDeMesure);
		}
		
		rep.delete(uniteDeMesure);
		
		Utilisateur user = utilisateurService.getByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
		logger.debug("L'administrateur "+user.getNom()+" "+user.getPrenom()+" ayant le Username "+user.getUsername()+" a supprimé l'unité de mesure "+uniteDeMesure.getDesignation());
		
	}


	public Optional<UniteDeMesure> getUniteDeMesureByDesignation(String designation)
	{
		Optional<UniteDeMesure> uniteDeMesure = rep.findByDesignation(designation);
		
		return uniteDeMesure;
	}

}
