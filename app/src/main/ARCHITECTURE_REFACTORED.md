# Architecture Refactorisée - Dossier Main

## Vue d'ensemble

Cette documentation décrit la structure refactorisée du dossier `main` de l'application Ganymede, avec un focus sur l'amélioration de l'organisation, la maintenabilité et les performances.

## Structure des fichiers principaux

### AndroidManifest.xml
- ✅ **Améliorations apportées :**
  - Documentation claire des permissions
  - Support des écrans pliables ajouté
  - Configuration optimisée pour le splash screen
  - Métadonnées Firebase mieux organisées

### MainActivity.kt
- ✅ **Architecture moderne :**
  - Implémentation du splash screen
  - Support Edge-to-Edge
  - Gestion intelligente des thèmes
  - Configuration automatique des barres système
  - Documentation complète des responsabilités

### GanymedeApplication.kt  
- ✅ **Fonctionnalités refactorisées :**
  - Initialisation Hilt avec WorkManager
  - Configuration Firebase et Crashlytics
  - Gestion intelligente de la mémoire
  - Logging conditionnel selon l'environnement
  - Pattern Singleton thread-safe

## Organisation des ressources

### Ressources Drawable (75+ fichiers)
Les fichiers drawable sont organisés par catégories logiques :

#### 🔌 Composants électroniques
- **ANSI Standard :** `ic_ansi_*` (BJT, capacités, diodes, LED, etc.)
- **IEC Standard :** `ic_iec_*` (mêmes composants, standard européen)
- **Circuits spécialisés :** Filtres, amplificateurs opérationnels

#### ⚡ Portes logiques
- **Portes de base :** AND, OR, NOT, NAND, NOR, XOR, XNOR
- **Standards :** ANSI et IEC pour chaque porte
- **Tables de vérité :** Représentations visuelles associées
- **Équations :** Formules mathématiques pour chaque porte

#### 🎨 Interface utilisateur
- **Thèmes :** `dark_mode.xml`, `light_mode.xml`
- **Icônes système :** `autorenew.xml`, launcher backgrounds
- **Ressources visuelles :** Logos, images de fond

#### 📐 Symboles et formules
- **Constantes physiques :** Symbols scientifiques
- **Équations :** Représentations LaTeX en XML
- **Diagrammes :** Circuits et schémas

## Optimisations apportées

### Performance
1. **Gestion mémoire améliorée** dans GanymedeApplication
2. **Edge-to-Edge UI** pour une expérience moderne
3. **Splash screen optimisé** avec délai contrôlé
4. **Lazy loading** des ressources

### Maintenabilité
1. **Documentation exhaustive** de chaque classe
2. **Séparation des responsabilités** claire
3. **Configuration centralisée** des composants
4. **Gestion d'erreurs robuste**

### Accessibilité
1. **Support RTL** maintenu
2. **Barres système adaptatives** selon le thème
3. **Configuration automatique** des contrastes

## Recommandations futures

### Organisation des drawables
Pour une meilleure organisation, considérer la création de sous-dossiers :
```
drawable/
├── components/         # Composants électroniques
│   ├── ansi/          # Standard ANSI
│   └── iec/           # Standard IEC
├── logic_gates/       # Portes logiques
│   ├── gates/         # Symboles des portes
│   ├── truth_tables/  # Tables de vérité
│   └── equations/     # Équations
├── ui/                # Éléments d'interface
└── symbols/           # Symboles et formules
```

### Architecture
1. **Modularisation** : Séparer les fonctionnalités en modules
2. **Tests** : Ajouter des tests unitaires pour les composants refactorisés
3. **CI/CD** : Intégrer des vérifications automatiques de l'architecture

## Migration et compatibilité

La refactorisation maintient la compatibilité avec :
- ✅ Toutes les versions Android supportées
- ✅ Les thèmes existants
- ✅ La navigation actuelle
- ✅ Les fonctionnalités Firebase

## Conclusion

Cette refactorisation améliore significativement :
- La **lisibilité** du code
- La **performance** de l'application  
- La **maintenabilité** à long terme
- L'**expérience utilisateur**

La structure est maintenant prête pour des évolutions futures et respecte les meilleures pratiques Android modernes.
