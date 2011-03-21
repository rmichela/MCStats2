package com.ryanmichela.MCStats2.controller;
//Copyright (C) 2010  Ryan Michela
//
//This program is free software: you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation, either version 3 of the License, or
//(at your option) any later version.
//
//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with this program.  If not, see <http://www.gnu.org/licenses/>.

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;


public class StatsEntityListener extends EntityListener {
	
	private StatsController controller;
	
	public StatsEntityListener(StatsController controller) {
		this.controller = controller;
	}

	@Override
	public void onEntityDeath(EntityDeathEvent event) {
		if(event.getEntity() instanceof Player) {
			controller.die((Player)event.getEntity());
		}
	}

	@Override
	public void onEntityDamage(EntityDamageEvent event) {
		if(!event.isCancelled()) {
			if(event instanceof EntityDamageByEntityEvent) {
				EntityDamageByEntityEvent hostileEvent = (EntityDamageByEntityEvent) event;
				if(hostileEvent.getEntity() instanceof LivingEntity && hostileEvent.getDamager() instanceof Player) {
					Player damager = (Player)hostileEvent.getDamager();
					LivingEntity damagee = (LivingEntity)hostileEvent.getEntity();
					if(damagee.getHealth() > 0 && damagee.getHealth() - event.getDamage() <= 0) {
						controller.kill(damager, damagee);
					}
				}
			}
		}
	}
	
	
}
