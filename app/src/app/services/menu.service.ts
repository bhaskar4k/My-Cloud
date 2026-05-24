import { Injectable } from '@angular/core';
import { MenuItem } from '../models/menu.model';
import menuData from '../constants/menu.json';

@Injectable({
    providedIn: 'root'
})
export class MenuService {
    constructor() { }

    getMenu(): MenuItem[] {
        return menuData;
    }
}
