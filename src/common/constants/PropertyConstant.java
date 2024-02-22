package common.constants;

import static common.constants.ApplicationConstants.FALSE;
import static common.constants.ApplicationConstants.TRUE;
import lombok.Getter;

@Getter
public enum PropertyConstant {
   
   SYSTEM_THEME("system.theme",SubstanceThemeConstant.BUSINESS_SKIN),
   GENERATE_TASK_ALMACEN("generate.task.almacen",TRUE),
   GENERATE_TASK_CHOFER("generate.task.chofer",TRUE),
   MAX_WIN_AGREGAR_RENTA("max.win.add.rent",FALSE),
   MAX_WIN_CONSULTAR_RENTA("max.win.consult.rent",FALSE),
   MAX_WIN_CONSULTAR_PROVEEDORES("max.win.consult.providers",FALSE),
   MAX_WIN_INVENTORY("max.win.inventory",FALSE);
   
   
   PropertyConstant (final String key, final String value) {
       this.key = key;
       this.value = value;
   }
   
   private final String key;
   private final String value;
}
