# 🦁 BarbenApp – Application Mobile du Zoo

BarbenApp est une application mobile immersive permettant aux visiteurs de naviguer dans le zoo, consulter les enclos et les animaux, interagir avec les services proposés, et donner leur avis. Elle intègre également des fonctionnalités spécifiques pour les employés et les administrateurs.

---

## 📱 Fonctionnalités Principales

### 🔐 Authentification
- **Login / Register** avec Firebase Authentication
- Gestion de sessions utilisateur persistantes
- Comptes spéciaux :
  - **Employé** : `email: employee@barbenapp.com` / `password: Employee123!`
  - **Admin** : `email: admin@barbenapp.com` / `password: Admin123!`

---

### 🏠 Page d’accueil du zoo
- Présentation générale du zoo
- Accès rapide aux enclos, services et carte

---

### 🗺️ Navigation interactive
- **Carte du zoo en temps réel**
  - Affichage des zones, chemins et services
  - Sélection du **point de départ** et **point d’arrivée**
  - Calcul d’itinéraires personnalisés
- **Accessibilité**
  - Possibilité de choisir un itinéraire **adapté aux personnes à mobilité réduite**

---

### 🦓 Enclos et Animaux
- Liste interactive de tous les **enclos** du zoo
- Détails de chaque enclos :
  - Liste des **animaux**
  - Position géographique sur la carte
  - Moyenne et liste des **avis** laissés par les visiteurs
- Affichage des horaires de **nourrissage** (employés et admins uniquement)

---

### 🧰 Services du Zoo
- Affichage de tous les services disponibles (restauration, sanitaires, boutiques…)
- Emplacement exact sur la carte

---

### 👤 Page Profil
- Récapitulatif du profil utilisateur
- Liste de tous les **avis** laissés sur les enclos

---

## 🛠️ Fonctions Spéciales (Employé / Admin)

### 👷 Employé
- Accès à la liste des **enclos**
- Visualisation des **heures de nourrissage** pour chaque enclos

### 👑 Administrateur
- Possède tous les droits des employés
- Peut **ouvrir/fermer un enclos**
- Peut **définir/modifier l’heure de nourrissage** pour chaque enclos

---

## 🧩 Technologies Utilisées
- **Kotlin** avec **Jetpack Compose**
- **Firebase RTDB** (données en temps réel)
- **Firebase Auth** (gestion utilisateurs)
- **OSMDroid** / Leaflet (intégration de carte géographique)

---

## 👨‍💻 Équipe de Développement

Cette application a été conçue et développée par :

- **Robin METAIS**
- **Lucas GUMUCHIAN**
- **Luigi GIUSIANO**

---

## 📜 Licence
Projet développé dans le cadre d’un projet universitaire — tous droits réservés.
