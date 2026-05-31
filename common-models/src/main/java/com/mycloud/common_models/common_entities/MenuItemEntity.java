package com.mycloud.common_models.common_entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MenuItemEntity {
   public Integer id;
   public String label;
   public String icon;
   public String route;
   public List<MenuItemEntity> submenu;
}
