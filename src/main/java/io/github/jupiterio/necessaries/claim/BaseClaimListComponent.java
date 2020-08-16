package io.github.jupiterio.necessaries.claim;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Iterator;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.text.Text;
import java.util.UUID;

public class BaseClaimListComponent implements ClaimListComponent {
    private List<Claim> claims = Lists.newArrayList();

    @Override
    public List<Claim> getClaims() {
        return claims;
    }

    @Override
    public int addClaim(Text name, UUID owner) {
        claims.add(new Claim(name, owner));
        return claims.size();
    }

    @Override
    public Claim getClaim(int id) {
        if (id == 0) {
            return Claim.WILDERNESS;
        } else {
            int index = id - 1;
            if (index >= claims.size()) {
                return Claim.UNKNOWN;
            } else {
                return claims.get(index);
            }
        }
    }

    @Override
    public void fromTag(CompoundTag tag) {
        claims.clear();

        ListTag claimList = tag.getList("Claims", 10);
        int claimsCount = claimList.size();

        for(int i = 0; i < claimsCount; ++i) {
            Claim claim = new Claim();
            claim.fromTag(claimList.getCompound(i));
            claims.add(claim);
        }
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        Iterator claimsIter = claims.iterator();
        ListTag claimList = new ListTag();

        while(claimsIter.hasNext()) {
            Claim claim = (Claim) claimsIter.next();
            claimList.add(claim.toTag(new CompoundTag()));
        }

        tag.put("Claims", claimList);

        return tag;
    }
}
