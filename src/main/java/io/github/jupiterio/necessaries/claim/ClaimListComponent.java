package io.github.jupiterio.necessaries.claim;

import dev.onyxstudios.cca.api.v3.component.Component;
import net.minecraft.text.Text;
import java.util.UUID;
import java.util.List;

public interface ClaimListComponent extends Component {
    List<Claim> getClaims();
    int addClaim(Text name, UUID owner);
    Claim getClaim(int id);
}
