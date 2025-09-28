Titre
=====
Installation locale de KaTeX pour Ganymede (assets)

But
---
Permettre à `KaTeXView` de charger KaTeX depuis les assets (offline) au lieu du CDN. Ce README montre comment récupérer les fichiers KaTeX minimaux et les placer dans le projet.

Emplacement cible dans le projet
--------------------------------
Copier les fichiers KaTeX ici :

  app/src/main/assets/katex/
  app/src/main/assets/katex/contrib/

Fichiers nécessaires (minimum)
------------------------------
- katex.min.js
- katex.min.css
- contrib/auto-render.min.js

Les fichiers doivent respecter ces chemins précis relatifs à `app/src/main/assets/` :

- app/src/main/assets/katex/katex.min.js
- app/src/main/assets/katex/katex.min.css
- app/src/main/assets/katex/contrib/auto-render.min.js

Méthode A — (rapide) télécharger depuis le CDN et copier localement (Windows cmd)
----------------------------------------------------------------------------------
Ouvrez un terminal cmd.exe à la racine du repo (là où se trouve `gradlew.bat`) et lancez :

```bat
mkdir app\src\main\assets\katex\contrib
curl -L -o app\src\main\assets\katex\katex.min.js https://cdn.jsdelivr.net/npm/katex@0.16.8/dist/katex.min.js
curl -L -o app\src\main\assets\katex\katex.min.css https://cdn.jsdelivr.net/npm/katex@0.16.8/dist/katex.min.css
curl -L -o app\src\main\assets\katex\contrib\auto-render.min.js https://cdn.jsdelivr.net/npm/katex@0.16.8/dist/contrib/auto-render.min.js
```

Si `curl` n'est pas disponible, utilisez PowerShell (toujours depuis la racine du repo) :

```powershell
mkdir -Force app\src\main\assets\katex\contrib
Invoke-WebRequest -Uri "https://cdn.jsdelivr.net/npm/katex@0.16.8/dist/katex.min.js" -OutFile "app\src\main\assets\katex\katex.min.js"
Invoke-WebRequest -Uri "https://cdn.jsdelivr.net/npm/katex@0.16.8/dist/katex.min.css" -OutFile "app\src\main\assets\katex\katex.min.css"
Invoke-WebRequest -Uri "https://cdn.jsdelivr.net/npm/katex@0.16.8/dist/contrib/auto-render.min.js" -OutFile "app\src\main\assets\katex\contrib\auto-render.min.js"
```

Méthode B — télécharger sur navigateur puis copier
---------------------------------------------------
1. Ouvrez dans votre navigateur :
   - https://cdn.jsdelivr.net/npm/katex@0.16.8/dist/katex.min.js
   - https://cdn.jsdelivr.net/npm/katex@0.16.8/dist/katex.min.css
   - https://cdn.jsdelivr.net/npm/katex@0.16.8/dist/contrib/auto-render.min.js
2. Enregistrez chaque fichier et copiez-les dans les dossiers ci-dessus dans votre projet.

Rebuild & tester
-----------------
Après avoir ajouté les fichiers locaux :

```bat
.\gradlew.bat assembleDebug
.\gradlew.bat installDebug
```

Lancer logcat pour vérifier le rendu (depuis une autre invite) :

```bat
adb logcat -s KaTeXView *:S
```

Vous devriez voir des lignes similaires à :
- KATEX_LOAD_STARTED
- KATEX_RENDER_AVAILABLE (si `auto-render` chargé)
- KATEX_TYPESAT_OK
- KATEX_HEIGHT:NNN

Si les messages indiquent que KaTeX local a été chargé (ou si CDN a été injecté), les formules devraient être correctement rendues.

Problèmes et résolutions rapides
--------------------------------
- Si `KATEX_LOCAL_NOT_READY - injecting CDN` apparait et que vous n'êtes pas en ligne, assurez-vous d'avoir correctement placé les fichiers dans `app/src/main/assets/katex/`.
- Si vous voyez des erreurs JS dans logcat, copiez-les et collez-les ici ; je vous aiderai à les corriger.

Conseils visuels
----------------
- Pour meilleure lisibilité sur images/fonds colorés, mettez `backgroundHex` non-transparent (p. ex. `#FFFFFF` ou la couleur `surface` du thème) dans les appels `KaTeXView(...)` dans `TransformerCalculatorInfo.kt`.
- Si les formules sont encore coupées, augmentez la valeur minimale `heightDp` (actuellement 120.dp) dans `KaTeXView`.

Souhaitez-vous que je copie ces fichiers dans le repo pour vous (vous fournissez l'archive), ou préférez-vous exécuter les commandes ci-dessus et me renvoyer les logs si un problème survient ?

