/*
 * Copyright (C) 2017 MCME
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.mcmiddleearth.perks.perks;


import com.mcmiddleearth.perks.permissions.Permissions;

import com.mcmiddleearth.perks.commands.PtimeHandler;
/**
 *
 * @author Fraspace5
 */
public class PtimePerk extends Perk {

   
    public PtimePerk() {
        super("ptime");
        setCommandHandler(new PtimeHandler(this, Permissions.USER.getPermissionNode()),"ptime");
    }
 
    
}
