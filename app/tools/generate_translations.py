#!/usr/bin/env python3
"""
generate_translations.py

Usage:
  python generate_translations.py [translations.csv]

This script reads the base English strings.xml (app/src/main/res/values/strings.xml)
and a CSV file containing translations and generates localized strings.xml files
under app/src/main/res/values-<locale>/strings.xml.

CSV format (sample in app/translations/sample_translations.csv):
key,en,fr,es,de
app_name,Ganymede,Ganymède,Ganimedes,Ganymed
...

If a translation cell is empty, the English value is used as fallback.
The script preserves the order of keys from the English file.
"""

import csv
import os
import sys
import xml.etree.ElementTree as ET
from xml.dom import minidom

PROJECT_ROOT = os.path.abspath(os.path.join(os.path.dirname(__file__), '..'))
RES_DIR = os.path.join(PROJECT_ROOT, 'src', 'main', 'res')
BASE_STRINGS = os.path.join(RES_DIR, 'values', 'strings.xml')


def read_base_strings():
    tree = ET.parse(BASE_STRINGS)
    root = tree.getroot()
    keys = []
    for child in root:
        if child.tag == 'string' and 'name' in child.attrib:
            keys.append((child.attrib['name'], (child.text or '').strip()))
    return keys


def write_strings(locale_header, translations, keys):
    # locale_header may be 'fr' or 'fr-rFR' etc. Create folder values-<locale_header>
    folder = os.path.join(RES_DIR, f'values-{locale_header}')
    os.makedirs(folder, exist_ok=True)
    out_path = os.path.join(folder, 'strings.xml')

    resources = ET.Element('resources')
    for key, default in keys:
        value = translations.get(key) or default or ''
        elem = ET.SubElement(resources, 'string')
        elem.set('name', key)
        elem.text = value

    xml_str = ET.tostring(resources, encoding='utf-8')
    pretty = minidom.parseString(xml_str).toprettyxml(indent='    ', encoding='utf-8')

    # minidom gives a byte string when encoding provided
    with open(out_path, 'wb') as f:
        f.write(pretty)
    print(f'Wrote {out_path}')


def load_csv(csv_path):
    with open(csv_path, newline='', encoding='utf-8') as f:
        reader = csv.DictReader(f)
        rows = list(reader)
    if not rows:
        raise SystemExit('CSV is empty or invalid')
    headers = reader.fieldnames
    if 'key' not in headers and 'name' not in headers:
        raise SystemExit("CSV must contain a 'key' column (string resource name)")
    key_col = 'key' if 'key' in headers else 'name'
    locales = [h for h in headers if h != key_col]

    translations_by_locale = {loc: {} for loc in locales}
    for row in rows:
        k = row.get(key_col).strip()
        if not k:
            continue
        for loc in locales:
            val = row.get(loc, '').strip()
            if val:
                translations_by_locale[loc][k] = val
    return translations_by_locale


def main():
    csv_path = sys.argv[1] if len(sys.argv) > 1 else os.path.join(PROJECT_ROOT, 'translations', 'sample_translations.csv')
    if not os.path.isfile(BASE_STRINGS):
        print('Base strings.xml not found at', BASE_STRINGS)
        sys.exit(1)
    if not os.path.isfile(csv_path):
        print('CSV file not found at', csv_path)
        sys.exit(1)

    keys = read_base_strings()
    translations = load_csv(csv_path)


    print('Done.')


if __name__ == '__main__':
    main()

