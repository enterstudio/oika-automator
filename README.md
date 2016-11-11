# Oika Automator

Automatisez vos tâches de conseiller [Oika Oika](http://www.oikaoika.fr/).

Cet outil se connecte à votre [espace réservé](http://www.oikaoika.fr/mon-espace-reserve/) et automatise certaines 
tâches.

- **Génération de cartes Identifiants** au format Word (**.docx**) pour communiquer les identifiants clients aux 
personnes ayant passé commande sur une animation (*ID Compte Client* et *Mot de Passe Internet*).
- (**TODO**) **Création des comptes clients** à partir d'une Feuille Excel.

### Prérequis

- [PhantomJS](http://phantomjs.org/) >= 2.1 (Le binaire doit être présent dans le PATH système)
- [Java](https://www.java.com/fr/) >= 8

### Execution

En ligne de commande, lancer le JAR de Oika Automator.

```
java -jar oika-automator.jar
```

La commande `help` permet d'afficher la liste des commandes disponibles. La touche `TAB` permet de proposer les 
différentes possibilités.

### Configuration des identifiants

Il est nécessaire de configurer l'identifiant et le mot de passe de connexion à l'espace réservé.

```
auth --login <identifiant> --password <mot-de-passe>
```

`<identifiant>` et `<mot-de-passe>` sont à remplacer par vos informations personelles.

Ces données restent mémorisées dans le fichier `oika.ini`

### Génération des cartes Identifiants

```
identifiants <nom-de-l-hotesse>
```

Cette commande permet de générer un fichier Word contenant les fiches à transmettre aux différents clients de la 
dernière réunion de l'hôte(sse). Chaque fiche contient le nom du client, son identifiant et son mot de passe.

Le fichier est généré dans le dossier courant, avec le nom de l'hôte(sse) comme nom de fichier.