import urllib.request
import os

drawables_dir = r"c:\Users\Administrator\Desktop\Androidstudio_travell_app\app\src\main\res\drawable"

assets = {
    "avatar_explorer.jpg": "https://lh3.googleusercontent.com/aida-public/AB6AXuCfdp-3hs8PwBq11K3X6wP-id-vK96qJxkQJcZZ3vo4Pxmr8PG2N3YHBMds7rscFWASf9pFJQi6U9cKqeWtJd2FVWjhJTrXc3cKhuYiCswhVlAt4HTBhoCcOnmJP_pXknaUfXXDl2gIaqg93pOl60uAFDeciS9M1f3j0WzzF6E-R-1oAGVi9QY6ucZPKs--PQPugIhRniDDaaw2JcL0Xrel19E6ai2w6y4w0RguDJEiuYIHwF8U6TW2",
    "iceland_highlands.jpg": "https://lh3.googleusercontent.com/aida-public/AB6AXuBjCd8Y37jC80jBj1tvEq9Agkx5lqiomxSoLFpI1rkyM3e_o1ZTg0W6MejN4_wjY-7iqoRPPFeSsQaeVX3MIZYarpP1UO0_lMoC3H43Uu5RGQJVc_Cf9J6Gd9vxF0-Z3RDw_lkZEXJ95qdPLZ2SqfZ4a7wxqZn_3KX0QlcDpl5MmXC_vSFwQFNmYnMqPzHqPhH4865H_jsk7bSWA0C7BmGFqyUGZzgzeHxXJcwyDtZon9dJZga62Yij",
    "thermal_map.jpg": "https://lh3.googleusercontent.com/aida-public/AB6AXuBeCkBY14OY8x5kXYKAVD0hhO2vv6ZLqqDvZ3FA7PnvbtdW7yjOpL_e8fhr-7Gfvp9s87ax-uYc1B1iYTZMi2eAq1SoHYoYPtM398vdetuFjsReDZbeMTG1QLBQI2Lc9YQN3pDjOxdy8RefXkPYrU0j-zPhHeQwUDSjlege8eVgpEFC216xToS-7bMZdekfpMr-DSvx4YBSVbBP6-W4oKC1FrNUrEPawy_Zj5kG6cRSlHF-h_UJgmCv",
    "alpine_outpost.jpg": "https://lh3.googleusercontent.com/aida-public/AB6AXuAXvJnD9F3ax_pyLyoYwNzK3MosI2UttbY1IbmBFcHqyk9bZ__BCy6IUsRNdksCTTXoz51LEcKoRFWqyerxPUiwu_QouH8ezqrobGBeQsKyCNldkBBHGdwLvUFERHhICmKbJw91h8wb-RE6Htqbm4YRCX5hR1TGZXVqUWZX7Pr6pdzN5RGOXo4KmJ1iZ8NlxKrzUyKB6q4mSl9nThkXydPjbWxuHsRybVfPeuKqZg6PxAvY-cGHNM7P",
    "frozen_pump.jpg": "https://lh3.googleusercontent.com/aida-public/AB6AXuDekRyZgqaab6napm6zCAxYHSRfgvdkpDC8Rt-t6JSoplNCPhZ998UOfip4otFhLgvOS1Ad5gFCm251jNpNAuuzRYI-R9UotIBQnlZsfQzCmejbXR7OHuazQZHGP0zERBvJx--BaFcuUY5sFm_Ih-MqkAIJt7-TjTQOvD588xB9cS0DYHdBWarZwUQgCygBq7cm0uAOMM90gPEzFLAiEYHgtAYEpFpB0QKd5rffX3Wp0RkgMT7mmto_",
    "shelter_interior.jpg": "https://lh3.googleusercontent.com/aida-public/AB6AXuBWhehlhPYxKFQA7L2m4XMeKwvqiD4AAwXwCeztU1kqVxDD1NwKeGqA0fDZ_05EwiXDUBTHvb6GdoQSLTcTJ07lwPRxfBrdQdyJZcXvWkFPKesT3474MQX_VS8XK7W9kHlH_ATUcnQtn5VGbIvGfC-aV-g8oI7QDVesTXG0AIB4OfwNzoUisVdkoispeqvtJ67TIX_bfC-n-8vshqTUHfzEufNwmgjWX8EDHaS3fF6HT8oCW229Frmz"
}

if not os.path.exists(drawables_dir):
    os.makedirs(drawables_dir)

for filename, url in assets.items():
    filepath = os.path.join(drawables_dir, filename)
    print(f"Downloading {filename} from {url}...")
    try:
        urllib.request.urlretrieve(url, filepath)
        print(f"Successfully saved to {filepath}")
    except Exception as e:
        print(f"Error downloading {filename}: {e}")

print("Assets downloading sequence finished.")
