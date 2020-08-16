package io.github.jupiterio.necessaries.claim;

import nerdhub.cardinal.components.api.component.Component;
import net.minecraft.text.Text;
import java.util.UUID;
import java.util.List;

public interface ClaimListComponent extends Component {
    List<Claim> getClaims();
    int addClaim(Text name, UUID owner);
    Claim getClaim(int id);
}
