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
   public Integer Id;
   public String Label;
   public String Icon;
   public String Route;
   public List<MenuItemEntity> Submenu;
}
