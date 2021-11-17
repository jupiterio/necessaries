package io.github.jupiterio.necessaries.claim;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Iterator;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
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
    public void readFromNbt(NbtCompound tag) {
        claims.clear();

        NbtList claimList = tag.getList("Claims", 10);
        int claimsCount = claimList.size();

        for(int i = 0; i < claimsCount; ++i) {
            Claim claim = new Claim();
            claim.fromTag(claimList.getCompound(i));
            claims.add(claim);
        }
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        Iterator claimsIter = claims.iterator();
        NbtList claimList = new NbtList();

        while(claimsIter.hasNext()) {
            Claim claim = (Claim) claimsIter.next();
            claimList.add(claim.toTag(new NbtCompound()));
        }

        tag.put("Claims", claimList);
    }
}
